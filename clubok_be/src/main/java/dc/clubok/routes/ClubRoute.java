package dc.clubok.routes;

import dc.clubok.db.controllers.ClubController;
import dc.clubok.db.models.Club;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import static dc.clubok.utils.Constants.*;

public class ClubRoute {
    private static Logger logger = LoggerFactory.getLogger(ClubRoute.class.getCanonicalName());

    public static Route GetClubs = (Request request, Response response) -> {
        logger.debug("GET /clubs");
//        TODO
        return notFound(response);
    };

    public static Route PostClubs = (Request request, Response response) -> {
        logger.debug("POST /clubs " + request.body());

        try {
            ClubController.createClub(gson.fromJson(request.body(), Club.class));
            return created(response);
        } catch (Exception e) {
            return badRequest(response, e);
        }
    };

    public static Route GetClubsId = (Request request, Response response) -> {
        logger.debug("GET /clubs/" + request.params(":id"));
//        TODO
        return notFound(response);
    };

    public static Route GetClubsIdSubscribers = (Request request, Response response) -> {
        logger.debug("GET /clubs/" + request.params(":id") + "/subscribers");
//        TODO
        return notFound(response);
    };

    public static Route GetClubsIdModerators = (Request request, Response response) -> {
        logger.debug("GET /clubs/" + request.params(":id") + "/moderators");
//        TODO
        return notFound(response);
    };

    public static Route GetClubsIdParticipants = (Request request, Response response) -> {
        logger.debug("GET /users/" + request.params(":id") + "/participants");
//        TODO
        return notFound(response);
    };

    public static Route DeleteClubsId = (Request request, Response response) -> {
        logger.debug("DELETE /clubs/" + request.params(":id"));
//        TODO
        return notFound(response);
    };

    public static Route PatchClubsId = (Request request, Response response) -> {
        logger.debug("PATCH /clubs/" + request.params(":id") + " " + request.body());
//        TODO
        return notFound(response);
    };
}
