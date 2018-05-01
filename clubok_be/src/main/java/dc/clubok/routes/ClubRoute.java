package dc.clubok.routes;

import dc.clubok.db.controllers.ClubController;
import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.Club;
import dc.clubok.db.models.User;
import dc.clubok.utils.ClubOkException;
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

            Document result = new Document("total", model.count(Club.class))
                    .append("results", ClubController.getClubs(size, page, orderBy, order, include, exclude));

            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route GetClubsId = (Request request, Response response) -> {
        try {
            Club club = ClubController.getClubById(request.params(":id"));
            if (club == null) {
                Document details = new Document("details", "Such club does not exist");
                throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
            }

            Document result = new Document("result", club);

            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route PatchClubsId = (Request request, Response response) -> {
        try {
            Document update = Document.parse(request.body());
            ClubController.editClub(request.params(":id"), update);
            return response(response, SC_NO_CONTENT, SUCCESS_EDIT);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route GetClubsIdSubscribers = (Request request, Response response) -> {
        try {
            Document result = new Document("results", ClubController.getSubscribersByClubId(request.params(":id")));
            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route PostClubsIdSubscribers = (Request request, Response response) -> {
        try {
            User user = UserController.getUserByToken(request.headers("x-auth"));

            ClubController.addSubscriber(request.params(":id"), user.getId().toHexString());
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

    public static Route GetClubsIdMembers = (Request request, Response response) -> {
        try {
            Document result = new Document("results", ClubController.getMembersByClubId(request.params(":id")));
            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route PostClubsIdMembers = (Request request, Response response) -> {
        try {
            User user = UserController.getUserByToken(request.headers("x-auth"));
            ClubController.addMember(request.params(":id"), user.getId().toHexString());

            return response(response, SC_NO_CONTENT, SUCCESS_MEMBERSHIP);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route DeleteClubsIdMembersId = (Request request, Response response) -> {
        try {
            ClubController.deleteMember(request.params(":id"), request.params(":uid"));

            return response(response, SC_NO_CONTENT, SUCCESS_UNMEMBERSHIP);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };
}
