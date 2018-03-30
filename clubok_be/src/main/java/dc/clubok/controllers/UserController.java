package dc.clubok.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import dc.clubok.Crypt;
import dc.clubok.models.Token;
import dc.clubok.models.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;

public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class.getCanonicalName());

    public static Route signUp = (Request request, Response response) -> {
        logger.debug("POST /users " + request.body());
        try {
            User user = gson.fromJson(request.body(), User.class);

            response.header("x-auth", generateAuthToken(user));
            model.saveOne(user, User.class);

            response.status(SC_CREATED);
            response.type(JSON);
            return user;
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route login = (Request request, Response response) -> {
        logger.debug("POST /users/login " + request.body());
        try {
            User user = findByCredentials(
                    gson.fromJson(request.body(), User.class).getEmail(),
                    gson.fromJson(request.body(), User.class).getPassword()
            );

            response.header("x-auth", generateAuthToken(user));
            model.update(user, new Document("tokens", user.getTokens()), User.class);

            response.status(SC_OK);
            response.type(JSON);
            return user;
        } catch (NullPointerException e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route fetchAllUsers = (Request request, Response response) -> {
        logger.debug("GET /users");

        try {
            response.type(JSON);
            response.status(SC_OK);
            return model.findAll(User.class);
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route getUserById = (Request request, Response response) -> {
        logger.debug("GET /users/" + request.params(":id"));

        try {
            User user = model.findById(new ObjectId(request.params(":id")), User.class);
            if (user == null) {
                response.type(JSON);
                response.status(SC_NOT_FOUND);
                return "";
            }

            response.type(JSON);
            response.status(SC_OK);
            return user;
        } catch (IllegalArgumentException e) {
            response.type(JSON);
            response.status(SC_NOT_FOUND);
            return "";
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route getPersonalInfo = (Request request, Response response) -> {
        logger.debug("GET /users/me " + request.headers("x-auth"));

        response.type(JSON);
        response.status(SC_OK);
        return findByToken(request.headers("x-auth"));
    };

    public static Route logout = (Request request, Response response) -> {
        logger.debug("DELETE /users/me/token " + request.headers("x-auth"));

        User user = findByToken(request.headers("x-auth"));
        if (user != null) {
            user.getTokens().remove(new Token("auth", request.headers("x-auth")));
            model.update(user, new Document("tokens", user.getTokens()), User.class);
        }

        response.type(JSON);
        response.status(SC_NO_CONTENT);
        return "";
    };

    public static Route logoutAll = (Request request, Response response) -> {
        logger.debug("DELETE /users/token/all");

        User user = findByToken(request.headers("x-auth"));
        model.update(user, new Document("tokens", new ArrayList<>()), User.class);

        response.type(JSON);
        response.status(SC_NO_CONTENT);
        return "";
    };

    public static Route getSubscriptionsByUserId = (Request request, Response response) -> {
        logger.debug("GET /users/" + request.params(":id") + "/subscriptions");
        try {
            User user = model.findById(new ObjectId(request.params(":id")), User.class);
            if (user == null) {
                response.type(JSON);
                response.status(SC_NOT_FOUND);
                return "";
            }
            response.type(JSON);
            response.status(SC_OK);
            return user.getSubscriptions();
        } catch (IllegalArgumentException e) {
            response.type(JSON);
            response.status(SC_NOT_FOUND);
            return "";
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route getTokensByUserId = (Request request, Response response) -> {
        logger.debug("GET /users/" + request.params(":id") + "/tokens");
        try{
            User user = model.findById(new ObjectId(request.params(":id")), User.class);
            if (user == null) {
                response.type(JSON);
                response.status(SC_NOT_FOUND);
                return "";
            }
            response.type(JSON);
            response.status(SC_OK);
            return user.getTokens();
        } catch (IllegalArgumentException e) {
            response.type(JSON);
            response.status(SC_NOT_FOUND);
            return "";
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route deleteUserById = (Request request, Response response) -> {
        logger.debug("DELETE /users/" + request.params(":id"));
        try {
            User user = model.findById(new ObjectId(request.params(":id")), User.class);
            if (user == null) {
                response.type(JSON);
                response.status(SC_NOT_FOUND);
                return "";
            }
            model.deleteOne(new Document("_id", user.getId()), User.class);
            response.type(JSON);
            response.status(SC_OK);
            return "";
        } catch (IllegalArgumentException e) {
            response.type(JSON);
            response.status(SC_NOT_FOUND);
            return "";
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route getPersonalSubscriptions = (Request request, Response response) -> {
        logger.debug("GET /users/me/subscriptions");
        ObjectId UserId = UserController.findByToken(request.headers("x-auth")).getId();
        response.type(JSON);
        response.status(SC_OK);
        return model.findById(UserId, User.class).getSubscriptions();
    };

    public static Route getPersonalTokens = (Request request, Response response) -> {
        logger.debug("GET /users/me/tokens");
        ObjectId UserId = UserController.findByToken(request.headers("x-auth")).getId();
        response.type(JSON);
        response.status(SC_OK);
        return model.findById(UserId, User.class).getTokens();
    };

    public static String generateAuthToken(User user) {
        String token = null;

        try {
            token = JWT.create()
                    .withIssuedAt(new Date(System.currentTimeMillis()))
                    .withClaim("id", user.getId().toHexString())
                    .withClaim("access", "auth")
                    .sign(Algorithm.HMAC256(config.getProperties().getProperty("secret")));

            user.getTokens().add(new Token("auth", token));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return token;
    }

    public static boolean authenticate(Request req, Response res) {
        logger.debug("authenticate()");
        String token = req.headers("x-auth");
        return token != null && findByToken(token) != null;
    }

    public static User findByCredentials(String email, String password)
            throws NullPointerException {
        User user = findByEmail(email);

        if (user != null && Crypt.compare(password.toCharArray(), user.getPassword())) {
            return user;
        } else
            throw new NullPointerException();
    }

    public static User findByEmail(String email) {
        return model.findByField("email", email, User.class);
    }

    public static User findByToken(String token) {
        logger.debug("findByToken()");
        String id = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(config.getProperties().getProperty("secret"));
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            id = jwt.getClaim("id").asString();
        } catch (UnsupportedEncodingException | JWTVerificationException exception) {
            logger.error(exception.getMessage());
        }

        if (id == null)
            return null;

        return model.findOne(new Document("_id", new ObjectId(id))
                        .append("tokens", new Token("auth", token)),
                User.class);
    }
}
