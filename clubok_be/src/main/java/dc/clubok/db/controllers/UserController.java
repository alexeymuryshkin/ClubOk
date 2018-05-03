package dc.clubok.db.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import dc.clubok.db.models.Token;
import dc.clubok.db.models.User;
import dc.clubok.utils.ClubOkException;
import dc.clubok.utils.Crypt;
import dc.clubok.utils.SearchParams;
import org.bson.Document;
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
            Document details = new Document("details", "Couldn't generate authentication token");
            throw new ClubOkException(ERROR_CREATE, details, SC_INTERNAL_SERVER_ERROR);
        }

        model.saveOne(user, User.class);
        return token;
    }

    public static Token loginUser(String email, String password) throws ClubOkException {
        User user = findByEmail(email);
        Token token;

        if (user == null) {
            Document details = new Document("details", "User with this email does not exist");
            throw new ClubOkException(ERROR_LOGIN, details);
        }

        if (Crypt.compare(password.toCharArray(), user.getPassword())) {
            try {
                token = generateAuthToken(user);
            } catch (UnsupportedEncodingException e) {
                Document details = new Document("details", "Couldn't generate authentication token");
                throw new ClubOkException(ERROR_LOGIN, details, SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            Document details = new Document("details", "Password is incorrect");
            throw new ClubOkException(ERROR_LOGIN, details);
        }

        model.addOneToSet(user, "tokens", token, User.class);

        return token;
    }

    public static List<User> getUsers(SearchParams params) throws ClubOkException {
        return model.findByParams(params, User.class);
    }

    public static User getUserByToken(String token) throws ClubOkException {
        try {
            Algorithm algorithm;
            algorithm = Algorithm.HMAC256(config.getProperties().getProperty("secret"));


            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);

            String id = jwt.getClaim("id").asString();
            return model.findOne(new Document("_id", new ObjectId(id))
                            .append("tokens", new Token("auth", token)),
                    User.class);
        } catch (UnsupportedEncodingException e) {
            Document details = new Document("details", "Incorrect authentication token");
            throw new ClubOkException(ERROR_QUERY, details, SC_UNAUTHORIZED);
        } catch (SignatureVerificationException sve) {
            Document details = new Document("details", "Token Verification Failed");
            throw new ClubOkException(ERROR_QUERY, details, SC_UNAUTHORIZED);
        }
    }

    public static User getUserById(String userId) throws ClubOkException {
        return model.findById(userId, User.class);
    }

    public static void logout(String token) throws Exception {
        User user = getUserByToken(token);
        if (user != null) {
            model.removeOneFromArray(user, "tokens", new Token("auth", token), User.class);
        }
    }

    public static void logoutAll(String token) throws ClubOkException {
        User user = getUserByToken(token);
        if (user == null) {
            Document details = new Document("details", "User was unauthorized or deleted");
            throw new ClubOkException(ERROR_LOGOUT, details);
        }
//        TODO Write tests
        model.update(user, new Document("tokens", new ArrayList<>()), User.class);
    }

    public static Set<ObjectId> getSubscriptionsByUserId(String userId) throws ClubOkException {
        Set<ObjectId> result;
        User user = model.findById(userId, User.class);
        if (user == null) {
            Document details = new Document("details", "Such user does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }
        result = user.getSubscriptions();

        return result;
    }

    public static void addSubscription(String userId, String clubId) throws ClubOkException {
        User user = getUserById(userId);
        if (user == null) {
            Document details = new Document("details", "Such user does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }
        model.addOneToSet(user, "subscriptions", clubId, User.class);
    }

    public static void deleteSubscription(String userId, String clubId) throws ClubOkException {
        User user = getUserById(userId);
        if (user == null) {
            Document details = new Document("details", "Such user does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }
        model.removeOneFromArray(user, "subscriptions", clubId, User.class);

    }

    public static List<Token> getTokensByUserId(String userId) throws ClubOkException {
        List<Token> result;
        User user = model.findById(userId, User.class);
        if (user == null) {
            Document details = new Document("details", "Such user does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }
        result = user.getTokens();

        return result;
    }

    public static void deleteUserById(String userId) throws ClubOkException {
        User user = model.findById(userId, User.class);
        if (user == null) {
            Document details = new Document("details", "Such user does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }
        model.deleteById(userId, User.class);
    }

    public static Set<ObjectId> getSubscriptionsByToken(String token) throws ClubOkException {
        Set<ObjectId> result;
        User user = getUserByToken(token);
        if (user == null) {
            Document details = new Document("details", "Such user does not exist or logged out");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }
        result = user.getSubscriptions();

        return result;
    }

    public static List<Token> getTokensByToken(String token) throws ClubOkException {
        List<Token> result;
        User user = getUserByToken(token);
        if (user == null) {
            Document details = new Document("details", "Such user does not exist or logged out");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
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
