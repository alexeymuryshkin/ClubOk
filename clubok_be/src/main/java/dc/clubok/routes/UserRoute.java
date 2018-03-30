package dc.clubok.routes;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import static dc.clubok.db.controllers.UserController.getUserByToken;
import static dc.clubok.utils.Constants.*;

public class UserRoute {
    private static Logger logger = LoggerFactory.getLogger(UserRoute.class.getCanonicalName());

    public static Route GetUsers = (Request request, Response response) -> {
        logger.debug("GET /users");
        try {
            return ok(response, UserController.getUsers(request.queryString()));
        } catch (Exception e) {
            return badRequest(response, e);
        }
    };

    public static Route PostUsers = (Request request, Response response) -> {
        logger.debug("POST /users " + request.body());

        try {
            User user = gson.fromJson(request.body(), User.class);

            response.header("x-auth", UserController.createUser(user).getToken());
            return created(response);
        } catch (Exception e) {
            return badRequest(response, e);
        }
    };

    public static Route PostUsersLogin = (Request request, Response response) -> {
        logger.debug("POST /users/login " + request.body());

        try {
            User user = gson.fromJson(request.body(), User.class);

            response.header("x-auth", UserController.loginUser(user.getEmail(), user.getPassword()).getToken());
            return ok(response);
        } catch (Exception e) {
            return badRequest(response, e);
        }
    };

    public static Route GetUsersMe = (Request request, Response response) -> {
        logger.debug("GET /users/me " + request.headers("x-auth"));

        try {
            return ok(response, getUserByToken(request.headers("x-auth")));
        } catch (Exception e) {
            return badRequest(response, e);
        }

    };

    public static Route GetUsersMeSubscriptions = (Request request, Response response) -> {
        logger.debug("GET /users/me/subscriptions " + request.headers("x-auth"));
//        TODO
        return notFound(response);
    };

    public static Route GetUsersMeTokens = (Request request, Response response) -> {
        logger.debug("GET /users/me/tokens " + request.headers("x-auth"));
//        TODO
        return notFound(response);
    };

    public static Route DeleteUsersMeTokens = (Request request, Response response) -> {
        logger.debug("DELETE /users/me/tokens " + request.headers("x-auth"));

        try {
            UserController.logoutAll(request.headers("x-auth"));
            return noContent(response);
        } catch (Exception e) {
            return badRequest(response, e);
        }
    };

    public static Route DeleteUsersMeToken = (Request request, Response response) -> {
        logger.debug("DELETE /users/me/token " + request.headers("x-auth"));
        try {
            UserController.logout(request.headers("x-auth"));
            return noContent(response);
        } catch (Exception e) {
            return badRequest(response, e);
        }
    };

    public static Route GetUsersId = (Request request, Response response) -> {
        logger.debug("GET /users/" + request.params(":id"));

        try {
            User user = UserController.getUserById(request.params(":id"));
            if (user == null) {
                return notFound(response);
            }
            return ok(response, user);
        } catch (IllegalArgumentException iae) {
            logger.debug("Incorrect Id format");
            return notFound(response);
        } catch (Exception e) {
            return badRequest(response, e);
        }
    };

    public static Route GetUsersIdSubscriptions = (Request request, Response response) -> {
        logger.debug("GET /users/" + request.params(":id") + "/subscriptions");
//        TODO
        return notFound(response);
    };

    public static Route GetUsersIdTokens = (Request request, Response response) -> {
        logger.debug("GET /users/" + request.params(":id") + "/tokens");
//        TODO
        return notFound(response);
    };

    public static Route DeleteUsersId = (Request request, Response response) -> {
        logger.debug("DELETE /users/" + request.params(":id"));
//        TODO
        return notFound(response);
    };

    public static boolean notAuthenticated(Request request, Response response) {
        logger.debug("authenticate()");
        String token = request.headers("x-auth");
        try {
            return token == null || getUserByToken(token) == null;
        } catch (SignatureVerificationException e) {
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return true;
        }
    }
}
