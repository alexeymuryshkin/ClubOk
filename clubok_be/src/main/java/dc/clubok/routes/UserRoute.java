package dc.clubok.routes;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.User;
import dc.clubok.utils.exceptions.ClubOkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import static dc.clubok.db.controllers.UserController.getUserByToken;
import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;

public class UserRoute {
    private static Logger logger = LoggerFactory.getLogger(UserRoute.class.getCanonicalName());

    public static Route GetUsers = (Request request, Response response) -> {
        logger.debug("GET /users");
        
        try {
            return response(response, SC_OK, UserController.getUsers(request.queryString()));
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route PostUsers = (Request request, Response response) -> {
        logger.debug("POST /users " + request.body());

        try {
            User user = gson.fromJson(request.body(), User.class);

            response.header("x-auth", UserController.createUser(user).getToken());
            return response(response, SC_CREATED, user.getId().toHexString());
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route PostUsersLogin = (Request request, Response response) -> {
        logger.debug("POST /users/login " + request.body());

        try {
            User user = gson.fromJson(request.body(), User.class);

            response.header("x-auth", UserController.loginUser(user.getEmail(), user.getPassword()).getToken());
            return response(response, SC_OK);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route GetUsersMe = (Request request, Response response) -> {
        logger.debug("GET /users/me " + request.headers("x-auth"));

        try {
            return response(response, SC_OK, getUserByToken(request.headers("x-auth")));
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }

    };

    public static Route GetUsersMeSubscriptions = (Request request, Response response) -> {
        logger.debug("GET /users/me/subscriptions " + request.headers("x-auth"));

        try {
            return response(response, SC_OK, UserController.getSubscriptionsByToken(request.headers("x-auth")));
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route GetUsersMeTokens = (Request request, Response response) -> {
        logger.debug("GET /users/me/tokens " + request.headers("x-auth"));

        try {
            return response(response, SC_OK, UserController.getTokensByToken(request.headers("x-auth")));
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route DeleteUsersMeTokens = (Request request, Response response) -> {
        logger.debug("DELETE /users/me/tokens " + request.headers("x-auth"));

        try {
            UserController.logoutAll(request.headers("x-auth"));
            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route DeleteUsersMeToken = (Request request, Response response) -> {
        logger.debug("DELETE /users/me/token " + request.headers("x-auth"));
        try {
            UserController.logout(request.headers("x-auth"));
            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route GetUsersId = (Request request, Response response) -> {
        logger.debug("GET /users/" + request.params(":id"));

        try {
            User user = UserController.getUserById(request.params(":id"));
            if (user == null) {
                throw new ClubOkException(USER_NOT_FOUND, "User with this id does not exist", SC_NOT_FOUND);
            }

            return response(response, SC_OK, user);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route GetUsersIdSubscriptions = (Request request, Response response) -> {
        logger.debug("GET /users/" + request.params(":id") + "/subscriptions");

        try {
            return response(response, SC_OK,  UserController.getSubscriptionsByUserId(request.params(":id")));
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route GetUsersIdTokens = (Request request, Response response) -> {
        logger.debug("GET /users/" + request.params(":id") + "/tokens");

        try{
            return response(response, SC_OK,  UserController.getTokensByUserId(request.params(":id")));
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route DeleteUsersId = (Request request, Response response) -> {
        logger.debug("DELETE /users/" + request.params(":id"));

        try {
            UserController.deleteUserById(request.params(":id"));
            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
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
