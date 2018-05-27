package dc.clubok.routes;

import dc.clubok.db.controllers.ClubController;
import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.Club;
import dc.clubok.db.models.User;
import dc.clubok.utils.ClubOkException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;

public class AdministrationRoute {
    private static Logger logger = LoggerFactory.getLogger(AdministrationRoute.class.getCanonicalName());

    public static Route PostAdministrationUsers = (Request request, Response response) -> {
        try {
            User user = gson.fromJson(request.body(), User.class);
            UserController.createUser(user);

            Document result = new Document("result", user);
            return response(response, SC_CREATED, SUCCESS_CREATE, result);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route PostAdministrationClubs = (Request request, Response response) -> {
        try {
            Club club = gson.fromJson(request.body(), Club.class);
            ClubController.createClub(club);

            Document result = new Document("club_id", club.getId().toHexString());
            return response(response, SC_CREATED, SUCCESS_CREATE, result);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route DeleteAdministrationClubsId = (Request request, Response response) -> {
        try {
            ClubController.deleteClubById(request.params(":id"));
            return response(response, SC_NO_CONTENT, SUCCESS_DELETE);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route DeleteAdministrationClubsIdSubscribersId = (Request request, Response response) -> {
        try {
            UserController.deleteSubscription(request.params(":uid"), request.params(":id"));
            ClubController.deleteSubscriber(request.params(":id"), request.params(":uid"));

            return response(response, SC_NO_CONTENT, SUCCESS_UNSUBSCRIPTION);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route GetAdministrationUsersIdTokens = (Request request, Response response) -> {
        try {
            Document result = new Document("result", UserController.getTokensByUserId(request.params(":id")));

            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route DeleteAdministrationUsersId = (Request request, Response response) -> {
        try {
            UserController.deleteUserById(request.params(":id"));
            return response(response, SC_NO_CONTENT, SUCCESS_DELETE);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };
}
