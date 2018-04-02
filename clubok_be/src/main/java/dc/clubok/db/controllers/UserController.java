package dc.clubok.db.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import dc.clubok.db.models.Token;
import dc.clubok.db.models.User;
import dc.clubok.utils.Crypt;
import dc.clubok.utils.exceptions.ClubOkException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Updates.set;
import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;

public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class.getCanonicalName());

    public static Token createUser(User user) throws ClubOkException {
        Token token;
        try {
            token = generateAuthToken(user);
        } catch (UnsupportedEncodingException e) {
            throw new ClubOkException(DB_SAVE_ERROR, "Couldn't generate authentication token", SC_INTERNAL_SERVER_ERROR);
        }

        model.saveOne(user, User.class);
        return token;
    }

    public static Token loginUser(String email, String password) throws ClubOkException {
        User user = findByEmail(email);
        Token token;

        if (user == null) {
            throw new ClubOkException(LOGIN_ERROR, "User with this email does not exist");
        }

        if (Crypt.compare(password.toCharArray(), user.getPassword())) {
            try {
                token = generateAuthToken(user);
            } catch (UnsupportedEncodingException e) {
                throw new ClubOkException(LOGIN_ERROR, "Couldn't generate authentication token", SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new ClubOkException(LOGIN_ERROR, "Password is incorrect");
        }

        model.update(user, set("tokens", user.getTokens()), User.class);

        return token;
    }

    public static List<User> getUsers(String params) throws ClubOkException {
//        TODO Add usage of query params
        return model.findAll(User.class);
    }

    public static User getUserByToken(String token) throws ClubOkException {
        logger.debug("getUserByToken()");

        Algorithm algorithm;
        try {
            algorithm = Algorithm.HMAC256(config.getProperties().getProperty("secret"));
        } catch (UnsupportedEncodingException e) {
            throw new ClubOkException(DB_QUERY_ERROR, "Incorrect authentication token");
        }
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);

        String id = jwt.getClaim("id").asString();

        return model.findOne(new Document("_id", new ObjectId(id))
                        .append("tokens", new Token("auth", token)),
                User.class);
    }

    public static User getUserById(String id) throws ClubOkException {
        return model.findById(id, User.class);
    }

    public static void logout(String token) throws Exception {
        User user = getUserByToken(token);

        if (user == null) {
            throw new ClubOkException(LOGOUT_ERROR, "User was unauthorized or deleted", SC_BAD_REQUEST);
        }
        user.getTokens().remove(new Token("auth", token));
//            TODO Change to delete only one token without replacing the whole array
        model.update(user, set("tokens", user.getTokens()), User.class);
    }

    public static void logoutAll(String token) throws ClubOkException {
        User user = getUserByToken(token);
        if (user == null) {
            throw new ClubOkException(LOGOUT_ERROR, "User was unauthorized or deleted", SC_BAD_REQUEST);
        }
//        TODO Write tests
        model.update(user, new Document("tokens", new ArrayList<>()), User.class);
    }

    public static Set<ObjectId> getSubscriptionsByUserId(String id) throws ClubOkException {
        Set<ObjectId> result;
        User user = model.findById(id, User.class);
        if (user == null) {
            throw new ClubOkException(USER_NOT_FOUND, "User does not exist", SC_NOT_FOUND);
        }
        result = user.getSubscriptions();

        return result;
    }

    public static List<Token> getTokensByUserId(String id) throws ClubOkException {
        List<Token> result;
        User user = model.findById(id, User.class);
        if (user == null) {
            throw new ClubOkException(USER_NOT_FOUND, "User does not exist", SC_NOT_FOUND);
        }
        result = user.getTokens();

        return result;
    }

    public static void deleteUserById(String id) throws ClubOkException {
        model.deleteById(id, User.class);
    }

    public static Set<ObjectId> getSubscriptionsByToken(String token) throws ClubOkException {
        Set<ObjectId> result;
        User user = getUserByToken(token);
        if (user == null) {
            throw new ClubOkException(USER_NOT_FOUND, "User does not exist", SC_NOT_FOUND);
        }
        result = user.getSubscriptions();

        return result;
    }

    public static List<Token> getTokensByToken(String token) throws ClubOkException {
        List<Token> result;
        User user = getUserByToken(token);
        if (user == null) {
            throw new ClubOkException(USER_NOT_FOUND, "User does not exist", SC_NOT_FOUND);
        }
        result = user.getTokens();

        return result;
    }

    public static Token generateAuthToken(User user) throws UnsupportedEncodingException {
        Token token = new Token("auth",
                JWT.create()
                        .withIssuedAt(new Date(System.currentTimeMillis()))
                        .withClaim("id", user.getId().toHexString())
                        .withClaim("access", "auth")
                        .sign(Algorithm.HMAC256(config.getProperties().getProperty("secret"))));

        user.getTokens().add(token);
        return token;
    }

    public static User findByEmail(String email) throws ClubOkException {
        return model.findByField("email", email, User.class);
    }

}
