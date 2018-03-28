package dc.clubok.mongomodel;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mongodb.client.MongoCollection;
import dc.clubok.ClubOKService;
import dc.clubok.Crypt;
import dc.clubok.models.*;
import dc.clubok.models.Model;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import javax.validation.ConstraintViolation;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;

public class MongoModel implements Model {
    private static Logger logger = LoggerFactory.getLogger(ClubOKService.class.getCanonicalName());

    private <T extends Entity> MongoCollection<T> getCollection(Class<T> type) {
        return ClubOKService.mongo.getDb().getCollection(
                type.getSimpleName().toLowerCase() + "s",
                type
        );
    }

    @Override
    public <T extends Entity> void save(T entity, Class<T> type) throws Exception {
        MongoCollection<T> collection = getCollection(type);
        validate(entity);

        if (entity instanceof User)
            ((User) entity).setPassword(Crypt.hash(((User) entity).getPassword().toCharArray()));

        collection.insertOne(entity);
        logger.info("Entity [" + entity.getClass().getSimpleName() + "] was created");
    }

    @Override
    public <T extends Entity> void saveMany(List<T> entities, Class<T> type) throws Exception {
        MongoCollection<T> collection = getCollection(type);

        for (Entity entity : entities) {
            validate(entity);
            if (entity instanceof User)
                ((User) entity).setPassword(Crypt.hash(((User) entity).getPassword().toCharArray()));
        }

        collection.insertMany(entities);
        logger.info("Entities [" + entities.get(0).getClass().getSimpleName() + "] were created");
    }

    @Override
    public <T extends Entity> long count(Class<T> type) {
        return getCollection(type).count();
    }

    @Override
    public <T extends Entity> T findById(ObjectId id, Class<T> type) {
        return getCollection(type).find(eq(id)).first();
    }

    @Override
    public <T extends Entity> List<T> findByIdAll(String fieldName, ObjectId id, Class<T> type) {
        return getCollection(type).find(eq(fieldName, id)).into(new ArrayList<>());
    }

    @Override
    public User findByEmail(String email) {
        return getCollection(User.class).find(eq("email", email)).first();
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
    public <T extends Entity> void update(T entity, Document update, Class<T> type) {
        getCollection(type).updateOne(eq("_id", entity.getId()), new Document("$set", update));
        logger.info("Entity [" + entity.getClass().getSimpleName() + "] was updated");
    }

    @Override
    public User findByToken(String token) {
        logger.debug("findByToken()");
        String id = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(ClubOKService.config.getProperties().getProperty("secret"));
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            id = jwt.getClaim("id").asString();
        } catch (UnsupportedEncodingException | JWTVerificationException exception) {
            logger.error(exception.getMessage());
        }

        if (id == null)
            return null;

        return getCollection(User.class).find(
                new Document("_id", new ObjectId(id))
                        .append("tokens", new Token("auth", token))
        ).first();
    }

    @Override
    public void removeToken(User user, String token) {
        user.getTokens().remove(new Token("auth", token));
        update(user, new Document("tokens", user.getTokens()), User.class);
    }

    @Override
    public void removeAllTokens(User user) {
        update(user, new Document("tokens", new ArrayList<>()), User.class);
    }

    @Override
    public boolean authenticate(Request req, Response res) {
        logger.debug("authenticate()");
        String token = req.headers("x-auth");
        return token != null && findByToken(token) != null;
    }

    @Override
    public <T extends Entity> void validate(T entity) throws Exception {
        logger.debug("Validating [" + entity.getClass().getSimpleName() + "]");
        Set<ConstraintViolation<Entity>> violations = ClubOKService.validator.validate(entity);

        String message = "";

        for (ConstraintViolation<Entity> violation: violations) {
            message += "Validation Error [" + entity.getClass().getSimpleName() + "] - " +
                    "Property: " + violation.getPropertyPath() +
                    "Value: " + violation.getInvalidValue() +
                    "Message: " + violation.getMessage() + "\n";
            logger.error(message);
        }

        if (violations.size() != 0)
            throw new Exception(message);

        if (entity instanceof User && findByEmail(((User) entity).getEmail()) != null) {
            message += "Validation Error: User with email " + ((User) entity).getEmail() + " already exists";
            logger.error(message);
            throw new Exception(message);
        }

    }

    @Override
    public <T extends Entity> void removeById(ObjectId id, Class<T> type) {
        getCollection(type).deleteOne(new Document("_id", id));
    }

    @Override
    public <T extends Entity> List<T> findByUserAll(User user, Class<T> type) {
        //TODO
        return null;
    }

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
