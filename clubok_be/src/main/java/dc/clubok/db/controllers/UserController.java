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
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;

public class UserController {
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

        model.addOneToSet(user, "tokens", token, User.class);

        return token;
    }

    public static List<User> getUsers(int size, int page, String orderBy, String order, Bson include, Bson exclude) throws ClubOkException {
        return model.findMany(size, page, orderBy, order, include, exclude, User.class);
    }

//    public List<User> getLastCreatedUsers(int size, int page) {
//
//    }

    public static User getUserByToken(String token) throws ClubOkException {
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

    public static User getUserById(String userId) throws ClubOkException {
        return model.findById(userId, User.class);
    }

    public static void logout(String token) throws Exception {
        User user = getUserByToken(token);
        if (user == null) {
            throw new ClubOkException(LOGOUT_ERROR, "User was unauthorized or deleted", SC_BAD_REQUEST);
        }

        model.removeOneFromArray(user, "tokens", new Token("auth", token), User.class);
    }

    public static void logoutAll(String token) throws ClubOkException {
        User user = getUserByToken(token);
        if (user == null) {
            throw new ClubOkException(LOGOUT_ERROR, "User was unauthorized or deleted", SC_BAD_REQUEST);
        }
//        TODO Write tests
        model.update(user, new Document("tokens", new ArrayList<>()), User.class);
    }

    public static Set<String> getSubscriptionsByUserId(String userId) throws ClubOkException {
        Set<String> result;
        User user = model.findById(userId, User.class);
        if (user == null) {
            throw new ClubOkException(USER_NOT_FOUND, "User does not exist", SC_NOT_FOUND);
        }
        result = user.getSubscriptions();

        return result;
    }

    public static void addSubscription(String userId, String clubId) throws ClubOkException {
        User user = getUserById(userId);
        if (user == null) {
            throw new ClubOkException(USER_NOT_FOUND, "User does not exist", SC_NOT_FOUND);
        }
        model.addOneToSet(user, "subscriptions", clubId, User.class);
    }

    public static void deleteSubscription(String userId, String clubId) throws ClubOkException {
        User user = getUserById(userId);
        if (user == null) {
            throw new ClubOkException(USER_NOT_FOUND, "User does not exist", SC_NOT_FOUND);
        }
        model.removeOneFromArray(user, "subscriptions", clubId, User.class);

    }

    public static List<Token> getTokensByUserId(String userId) throws ClubOkException {
        List<Token> result;
        User user = model.findById(userId, User.class);
        if (user == null) {
            throw new ClubOkException(USER_NOT_FOUND, "User does not exist", SC_NOT_FOUND);
        }
        result = user.getTokens();

        return result;
    }

    public static void deleteUserById(String userId) throws ClubOkException {
        model.deleteById(userId, User.class);
    }

    public static Set<String> getSubscriptionsByToken(String token) throws ClubOkException {
        Set<String> result;
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
