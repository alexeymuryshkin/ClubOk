package dc.clubok.routes;

import dc.clubok.db.controllers.ClubController;
import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.club.Club;
import dc.clubok.db.models.user.User;
import dc.clubok.utils.exceptions.ClubOkException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.include;
import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;

public class ClubRoute {
    private static Logger logger = LoggerFactory.getLogger(ClubRoute.class.getCanonicalName());

    public static Route GetClubs = (Request request, Response response) -> {
        try {
            int size = request.queryParams("size") == null ? 50 : Integer.parseInt(request.queryParams("size"));
            int page = request.queryParams("page") == null ? 1 : Integer.parseInt(request.queryParams("page"));
            String orderBy = request.queryParams("orderBy") == null ? "" : request.queryParams("orderBy");
            String order = request.queryParams("order") == null ? "ascending" : request.queryParams("order");

            Bson include = include();
            Bson exclude = exclude();

            Document document = new Document("total", model.count(Club.class))
                    .append("results", ClubController.getClubs(size, page, orderBy, order, include, exclude));

            return response(response, SC_OK, document);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route PostClubs = (Request request, Response response) -> {
        try {
            Club club = gson.fromJson(request.body(), Club.class);
            ClubController.createClub(club);
            User user = UserController.getUserByToken(request.headers("x-auth"));
            ClubController.addModerator(club.getId().toHexString(), user.getId().toHexString());
            return response(response, SC_CREATED, club.getId().toHexString());
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route GetClubsId = (Request request, Response response) -> {
        try {
            Club club = ClubController.getClubById(request.params(":id"));
            if (club == null) {
                throw new ClubOkException(CLUB_NOT_FOUND, "Club does not exist", SC_NOT_FOUND);
            }

            return response(response, SC_OK, club);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route DeleteClubsId = (Request request, Response response) -> {
        try {
            ClubController.deleteClubById(request.params(":id"));
            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route PatchClubsId = (Request request, Response response) -> {
        try {
            Document update = Document.parse(request.body());
            ClubController.editClub(request.params(":id"), update);
            return response(response, SC_OK);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route GetClubsIdSubscribers = (Request request, Response response) -> {
        try {
            return response(response, SC_OK, ClubController.getSubscribersByClubId(request.params("id")));
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route PostClubsIdSubscribers = (Request request, Response response) -> {
        try {
            User user = UserController.getUserByToken(request.headers("x-auth"));

            ClubController.addSubscriber(request.params("id"), user.getId().toHexString());
            UserController.addSubscription(user.getId().toHexString(), request.params("id"));
            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route DeleteClubsIdSubscribersId = (Request request, Response response) -> {
        try {
            UserController.deleteSubscription(request.params(":uid"), request.params(":id"));
            ClubController.deleteSubscriber(request.params(":id"), request.params(":uid"));

            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route GetClubsIdModerators = (Request request, Response response) -> {
        try {
            return response(response, SC_OK, ClubController.getModeratorsByClubId(request.params("id")));
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route PostClubsIdModerators = (Request request, Response response) -> {
        try {
            User user = UserController.getUserByToken(request.headers("x-auth"));

            ClubController.addModerator(request.params("id"), user.getId().toHexString());
            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route DeleteClubsIdModeratorsId = (Request request, Response response) -> {
        try {
            ClubController.deleteModerator(request.params(":id"), request.params(":uid"));

            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route GetClubsIdMembers = (Request request, Response response) -> {
        try {
            return response(response, SC_OK, ClubController.getMembersByClubId(request.params("id")));
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route PostClubsIdMembers = (Request request, Response response) -> {
        try {
            User user = UserController.getUserByToken(request.headers("x-auth"));

            ClubController.addMember(request.params("id"), user.getId().toHexString());
            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route DeleteClubsIdMembersId = (Request request, Response response) -> {
        try {
            ClubController.deleteParticipant(request.params(":id"), request.params(":uid"));

            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };
}
