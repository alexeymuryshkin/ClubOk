package dc.clubok.routes;

import dc.clubok.db.controllers.ClubController;
import dc.clubok.db.models.Club;
import dc.clubok.utils.exceptions.ClubOkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import static dc.clubok.utils.Constants.gson;
import static dc.clubok.utils.Constants.response;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

public class ClubRoute {
    private static Logger logger = LoggerFactory.getLogger(ClubRoute.class.getCanonicalName());

    public static Route GetClubs = (Request request, Response response) -> {
        logger.debug("GET /clubs");
//        TODO
        return response(response, SC_NOT_FOUND);
    };

    public static Route PostClubs = (Request request, Response response) -> {
        logger.debug("POST /clubs " + request.body());

        try {
            ClubController.createClub(gson.fromJson(request.body(), Club.class));
            return response(response, SC_CREATED);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        }
    };

    public static Route GetClubsId = (Request request, Response response) -> {
        logger.debug("GET /clubs/" + request.params(":id"));
//        TODO
        return response(response, SC_NOT_FOUND);
    };

    public static Route GetClubsIdSubscribers = (Request request, Response response) -> {
        logger.debug("GET /clubs/" + request.params(":id") + "/subscribers");
//        TODO
        return response(response, SC_NOT_FOUND);
    };

    public static Route GetClubsIdModerators = (Request request, Response response) -> {
        logger.debug("GET /clubs/" + request.params(":id") + "/moderators");
//        TODO
        return response(response, SC_NOT_FOUND);
    };

    public static Route GetClubsIdParticipants = (Request request, Response response) -> {
        logger.debug("GET /users/" + request.params(":id") + "/participants");
//        TODO
        return response(response, SC_NOT_FOUND);
    };

    public static Route DeleteClubsId = (Request request, Response response) -> {
        logger.debug("DELETE /clubs/" + request.params(":id"));
//        TODO
        return response(response, SC_NOT_FOUND);
    };

    public static Route PatchClubsId = (Request request, Response response) -> {
        logger.debug("PATCH /clubs/" + request.params(":id") + " " + request.body());
//        TODO
        return response(response, SC_NOT_FOUND);
    };
}
