package dc.clubok.controllers;

import dc.clubok.models.Club;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.*;

import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;

public class ClubController {
    private static Logger logger = LoggerFactory.getLogger(ClubController.class.getCanonicalName());

    public static Route createClub = (Request request, Response response) -> {
        logger.debug("POST /clubs " + request.body());
        try {
            Club club = gson.fromJson(request.body(), Club.class);
            model.saveOne(club, Club.class);

            response.type(JSON);
            response.status(SC_OK);
            return club;
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route fetchAllClubs = (Request request, Response response) -> {
//        TODO
        return "";
    };

    public static Route getClubById = (Request request, Response response) -> {
//        TODO
        return "";
    };

    public static Route getSubscribersByClubId = (Request request, Response response) -> {
//        TODO
        return "";
    };

    public static Route getModeratorsByClubId = (Request request, Response response) -> {
//        TODO
        return "";
    };

    public static Route getParticipantsByClubId = (Request request, Response response) -> {
//        TODO
        return "";
    };

    public static Route deleteClubById = (Request request, Response response) -> {
//        TODO
        return "";
    };

    public static Route editClubInfoById = (Request request, Response response) -> {
//        TODO
        return "";
    };
}
