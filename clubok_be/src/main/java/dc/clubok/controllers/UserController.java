package dc.clubok.controllers;

import com.google.gson.Gson;
import dc.clubok.ClubOKService;
import dc.clubok.models.Model;
import dc.clubok.models.User;
import dc.clubok.mongomodel.MongoModel;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.*;

import static org.apache.http.HttpStatus.*;

public class UserController {
    private static final String JSON = "application/json";
    private static Logger logger = LoggerFactory.getLogger(UserController.class.getCanonicalName());
    private static final Gson gson = new Gson();
    private static final Model model = ClubOKService.model;

    public static Route signUp = (Request request, Response response) -> {
        logger.debug("POST /users " + request.body());
        try {
            User user = gson.fromJson(request.body(), User.class);

            response.header("x-auth", MongoModel.generateAuthToken(user));
            model.save(user, User.class);

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
            User user = model.findByCredentials(
                    gson.fromJson(request.body(), User.class).getEmail(),
                    gson.fromJson(request.body(), User.class).getPassword()
            );

            response.header("x-auth", MongoModel.generateAuthToken(user));
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
        return model.findByToken(request.headers("x-auth"));
    };

    public static Route logout = (Request request, Response response) -> {
        logger.debug("DELETE /users/me/token " + request.headers("x-auth"));

        User user = model.findByToken(request.headers("x-auth"));
        model.removeToken(user, request.headers("x-auth"));

        response.type(JSON);
        response.status(SC_NO_CONTENT);
        return "";
    };

    public static Route logoutAll = (Request request, Response response) -> {
        logger.debug("DELETE /users/token/all");

        User user = model.findByToken(request.headers("x-auth"));
        model.removeAllTokens(user);

        response.type(JSON);
        response.status(SC_NO_CONTENT);
        return "";
    };

    public static Route getSubscriptionsByUserId = (Request request, Response response) -> {
//        TODO
        return "";
    };
    public static Route getTokensByUserId = (Request request, Response response) -> {
//        TODO
        return "";
    };

    public static Route deleteUserById = (Request request, Response response) -> {
//        TODO
        return "";
    };
}
