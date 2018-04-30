package dc.clubok.routes;

import dc.clubok.db.controllers.ClubController;
import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.user.User;
import dc.clubok.utils.exceptions.ClubOkException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;

import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.include;
import static dc.clubok.db.controllers.UserController.getUserByToken;
import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;

public class UserRoute {
    private static Logger logger = LoggerFactory.getLogger(UserRoute.class.getCanonicalName());

    public static Route GetUsers = (Request request, Response response) -> {
        try {
            int size = request.queryParams("size") == null ? 50 : Integer.parseInt(request.queryParams("size"));
            int page = request.queryParams("page") == null ? 1 : Integer.parseInt(request.queryParams("page"));
            String orderBy = request.queryParams("orderBy") == null ? "" : request.queryParams("orderBy");
            String order = request.queryParams("order") == null ? "ascending" : request.queryParams("order");

            Bson include = include();
            Bson exclude = exclude("password", "tokens", "subscriptions");

            Document document = new Document("total", model.count(User.class))
                    .append("results", UserController.getUsers(size, page, orderBy, order, include, exclude));

            return response(response, SC_OK, document);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route PostUsers = (Request request, Response response) -> {
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

    public static Route DeleteUsersMeSubscriptionsId = (Request request, Response response) -> {
        try {
            UserController.deleteSubscription(request.headers("x-auth"), request.params(":id"));
            ClubController.deleteSubscriber(request.params("id"), request.headers("x-auth"));
            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route GetUsersMeTokens = (Request request, Response response) -> {
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

    public static Filter Authenticate = (Request request, Response response) -> {
        String token = request.headers("x-auth");
        try {
            getUserByToken(token);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };
}
