package dc.clubok.routes;

import dc.clubok.db.controllers.ClubController;
import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.User;
import dc.clubok.utils.ClubOkException;
import dc.clubok.utils.SearchParams;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.fields;
import static dc.clubok.db.controllers.UserController.getUserByToken;
import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;
import static spark.Spark.halt;

public class UserRoute {
    private static Logger logger = LoggerFactory.getLogger(UserRoute.class.getCanonicalName());

    public static Route GetUsers = (Request request, Response response) -> {
        try {
            SearchParams params = new SearchParams(request.queryMap().toMap());
            params.setProjection(fields(exclude("password", "tokens", "subscriptions")));

            List<User> users = UserController.getUsers(params);

            Document result = new Document("total", users.size())
                    .append("result", users);

            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route PostUsers = (Request request, Response response) -> {
        try {
            if (request.headers("x-auth") == null) {
                User user = gson.fromJson(request.body(), User.class);
                response.header("x-auth", UserController.createUser(user).getToken());

            Document result = new Document("user_id", user.getId());

                return response(response, SC_CREATED, SUCCESS_REGISTRATION, result);
            } else {
                Document details = new Document("details", "Regular users cannot create new users. Please logout for new signup");
                throw new ClubOkException(ERROR_UNAUTHORIZED_REQUEST, details);
            }
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route PostUsersLogin = (Request request, Response response) -> {
        try {
            if (request.headers("x-auth") == null) {
                User user = gson.fromJson(request.body(), User.class);
                response.header("x-auth", UserController.loginUser(user.getEmail(), user.getPassword()).getToken());

                return response(response, SC_NO_CONTENT, SUCCESS_LOGIN);
            } else {
                Document details = new Document("details", "You have already logged in");
                throw new ClubOkException(ERROR_UNAUTHORIZED_REQUEST, details);
            }
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route GetUsersMe = (Request request, Response response) -> {
        try {
            Document result = new Document("result", getUserByToken(request.headers("x-auth")));
            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route GetUsersMeSubscriptions = (Request request, Response response) -> {
        try {
            Document result = new Document("result", UserController.getSubscriptionsByToken(request.headers("x-auth")));
            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route PostUsersMeSubscriptions = (Request request, Response response) -> {
        try {
            User user = getUserByToken(request.headers("x-auth"));
            UserController.addSubscription(user.getId().toHexString(), request.params(":id"));

            return response(response, SC_NO_CONTENT, SUCCESS_SUBSCRIPTION);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route DeleteUsersMeSubscriptionsId = (Request request, Response response) -> {
        try {
            User user = UserController.getUserByToken(request.headers("x-auth"));
            UserController.deleteSubscription(user.getId().toHexString(), request.params(":id"));
            ClubController.deleteSubscriber(request.params("id"), user.getId().toHexString());

            return response(response, SC_NO_CONTENT, SUCCESS_UNSUBSCRIPTION);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route GetUsersMeTokens = (Request request, Response response) -> {
        try {
            Document result = new Document("result", UserController.getTokensByToken(request.headers("x-auth")));
            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route DeleteUsersMeTokens = (Request request, Response response) -> {
        try {
            UserController.logoutAll(request.headers("x-auth"));
            return response(response, SC_NO_CONTENT, SUCCESS_LOGOUT);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route DeleteUsersMeToken = (Request request, Response response) -> {
        try {
            UserController.logout(request.headers("x-auth"));
            return response(response, SC_NO_CONTENT, SUCCESS_LOGOUT);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route GetUsersId = (Request request, Response response) -> {
        try {
            User user = UserController.getUserById(request.params(":id"));

            if (user == null) {
                Document details = new Document("details", "Such user does not exist");
                throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
            }
            Document result = new Document("result", user);
            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route GetUsersIdSubscriptions = (Request request, Response response) -> {
        try {
            Document result = new Document("result", UserController.getSubscriptionsByUserId(request.params(":id")));
            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Filter Authenticate = (Request request, Response response) -> {
        String token = request.headers("x-auth");
        try {
            if (token != null) {
                getUserByToken(token);
            } else {
                throw new ClubOkException(TOKEN_ERROR, "Authentication token is not provided", SC_UNAUTHORIZED);
            }
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            halt(e.getStatusCode());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
            halt(SC_INTERNAL_SERVER_ERROR);
        }
    };
}
