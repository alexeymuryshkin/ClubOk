package dc.clubok.db.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import dc.clubok.db.models.Token;
import dc.clubok.db.models.User;
import dc.clubok.utils.Crypt;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static dc.clubok.utils.Constants.config;
import static dc.clubok.utils.Constants.model;

public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class.getCanonicalName());

    public static Token createUser(User user) throws Exception {
        Token token = generateAuthToken(user);
        model.saveOne(user, User.class);
        return token;
    }

    public static Token loginUser(String email, String password) throws Exception {
        User user = findByEmail(email);
        Token token;

        if (Crypt.compare(password.toCharArray(), user.getPassword())) {
            token = generateAuthToken(user);
        } else {
            throw new NullPointerException();
        }

        model.update(user, new Document("tokens", user.getTokens()), User.class);

        return token;
    }

    public static List<User> getUsers(String params) throws Exception {
//        TODO Add usage of query params
        return model.findAll(User.class);
    }

    public static User getUserByToken(String token) throws Exception {
        logger.debug("getUserByToken()");

        Algorithm algorithm = Algorithm.HMAC256(config.getProperties().getProperty("secret"));
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);

        String id = jwt.getClaim("id").asString();

        return model.findOne(new Document("_id", new ObjectId(id))
                        .append("tokens", new Token("auth", token)),
                User.class);
    }

    public static User getUserById(String id) throws Exception {
        return model.findById(id, User.class);
    }

    public static void logout(String token) throws Exception {
        User user = getUserByToken(token);

        if (user != null) {
            user.getTokens().remove(new Token("auth", token));
            model.update(user, new Document("tokens", user.getTokens()), User.class);
        }
    }

    public static void logoutAll(String token) throws Exception {
        User user = getUserByToken(token);
        model.update(user, new Document("tokens", new ArrayList<>()), User.class);
    }

    public static List<ObjectId> getSubscriptionsByUserId(String id) {
//        TODO
        return null;
    }

    public static List<Token> getTokensByUserId(String id) {
//        TODO
        return null;
    }

    public static void deleteUserById (String id) {
//        TODO
    }

    public static List<ObjectId> getSubscriptionsByToken (String token) {
//        TODO
        return null;
    }

    public static List<Token> getTokensByToken (String token) {
//        TODO
        return null;
    }

    public static Token generateAuthToken(User user) throws Exception {
        Token token = new Token("auth",
                JWT.create()
                        .withIssuedAt(new Date(System.currentTimeMillis()))
                        .withClaim("id", user.getId().toHexString())
                        .withClaim("access", "auth")
                        .sign(Algorithm.HMAC256(config.getProperties().getProperty("secret"))));

        user.getTokens().add(token);
        return token;
    }

    public static User findByEmail(String email) {
        return model.findByField("email", email, User.class);
    }

}
