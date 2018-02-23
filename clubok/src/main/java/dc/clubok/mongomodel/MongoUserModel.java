package dc.clubok.mongomodel;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mongodb.client.MongoCollection;
import dc.clubok.ClubOKService;
import dc.clubok.Crypt;
import dc.clubok.entities.Entity;
import dc.clubok.entities.Token;
import dc.clubok.entities.User;
import dc.clubok.entities.models.UserModel;
import org.bson.Document;
import org.bson.types.ObjectId;
import spark.Request;
import spark.Response;

import javax.validation.ConstraintViolation;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;


public class MongoUserModel
        implements UserModel {
    private MongoCollection<User> collection;

    public MongoUserModel() {
        collection = ClubOKService.mongo.getDb().getCollection("users", User.class);
    }

    // INSERT METHODS
    @Override
    public void save(User user) throws Exception {
        validate(user);
        user.setPassword(Crypt.hash(user.getPassword().toCharArray()));

        collection.insertOne(user);
        System.out.println("User has been created");
    }

    @Override
    public void saveMany(List<User> users) throws Exception {
        for (User user : users) {
            validate(user);
            user.setPassword(Crypt.hash(user.getPassword().toCharArray()));
        }
        collection.insertMany(users);
        System.out.println("Users have been created");
    }

    // FIND METHODS
    @Override
    public long count() {
        return collection.count();
    }

    @Override
    public User findById(ObjectId id) {
        return collection.find(eq(id)).first();
    }

    @Override
    public User findByEmail(String email) {
        return collection.find(eq("email", email)).first();
    }

    @Override
    public User findByCredentials(String email, String password)
            throws NullPointerException {
        User user = findByEmail(email);

        if (user != null && Crypt.compare(password.toCharArray(), user.getPassword())) {
            return user;
        } else
            throw new NullPointerException();

    }

    @Override
    public User findByToken(String token) {
        String id = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(ClubOKService.config.getProperties().getProperty("secret"));
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            id = jwt.getClaim("id").asString();
        } catch (UnsupportedEncodingException | JWTVerificationException exception) {
            //UTF-8 encoding not supported
        }

        if (id == null)
            return null;

        return collection.find(
                new Document("_id", new ObjectId(id))
                        .append("tokens", new Token("auth", token))
        ).first();
    }

    @Override
    public boolean authenticate(Request req, Response res) {
        String token = req.headers("x-auth");
        return token != null && findByToken(token) != null;
    }

    // UPDATE METHODS
    @Override
    public void update(User user, Document update) {
        collection.updateOne(eq("_id", user.getId()), new Document("$set", update));
    }

    @Override
    public void removeToken(User user, String token) {
        user.getTokens().remove(new Token("auth", token));
        update(user, new Document("tokens", user.getTokens()));
    }


    public void validate(User user)
            throws Exception {
        Set<ConstraintViolation<Entity>> violations = ClubOKService.validator.validate(user);

        StringBuilder message = new StringBuilder();

        for (ConstraintViolation<Entity> violation: violations) {
            message
                    .append("VALIDATION ERROR:\n")
                    .append("Property: ").append(violation.getPropertyPath())
                    .append(", Value: ").append(violation.getInvalidValue())
                    .append(", Message: ").append(violation.getMessage())
                    .append("\n");
        }

        if (violations.size() != 0)
            throw new Exception(message.toString());

        if (findByEmail(user.getEmail()) != null)
            throw new Exception("VALIDATION ERROR: User with email " + user.getEmail() + " already exists");
    }

    // DELETE METHODS


    // OTHER METHODS
    public static String generateAuthToken(User user) {
        String token = null;

        try {
            token = JWT.create()
                    .withIssuedAt(new Date(System.currentTimeMillis()))
                    .withClaim("id", user.getId().toHexString())
                    .withClaim("access", "auth")
                    .sign(Algorithm.HMAC256(ClubOKService.config.getProperties().getProperty("secret")));

            user.getTokens().add(new Token("auth", token));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return token;
    }

}
