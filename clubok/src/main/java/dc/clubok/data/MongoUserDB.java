package dc.clubok.data;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mongodb.client.MongoCollection;
import dc.clubok.App;
import dc.clubok.models.Token;
import dc.clubok.models.User;
import org.bson.Document;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;


public class MongoUserDB{
    private static MongoCollection<User> collection = App.mongo.getDb().getCollection("users", User.class);

    public static void save(User user) {
        collection.insertOne(user);
        System.out.println("User has been created");
    }

    public static void update(User user, Document update) {
        collection.updateOne(eq("_id", user.getId()), new Document("$set", update));
    }

    public static String generateAuthToken(User user) {
        String token = null;

        try {
            token = JWT.create()
                    .withIssuedAt(new Date(System.currentTimeMillis()))
                    .withClaim("id", user.getId().toHexString())
                    .withClaim("access", "auth")
                    .sign(Algorithm.HMAC256(App.config.getProperties().getProperty("secret")));

            user.getTokens().add(new Token("auth", token));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return token;
    }

    public static User findByCredentials(String email, String password)
            throws NullPointerException {
        User user = collection.find(eq("email", email)).first();

        if (user != null && Crypt.compare(password.toCharArray(), user.getPassword())) {
            return user;
        } else
            throw new NullPointerException();

    }
}
