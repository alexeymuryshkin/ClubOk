package dc.clubok.controllers;

import com.google.gson.Gson;
import dc.clubok.ClubOKService;
import dc.clubok.models.Club;
import dc.clubok.models.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.*;

import static org.apache.http.HttpStatus.*;

public class ClubController {
    private static final String JSON = "application/json";
    private static Logger logger = LoggerFactory.getLogger(ClubController.class.getCanonicalName());
    private static final Gson gson = new Gson();
    private static final Model model = ClubOKService.model;

    public static Route createClub = (Request request, Response response) -> {
        logger.debug("POST /clubs " + request.body());
        try {
            Club club = gson.fromJson(request.body(), Club.class);
            model.save(club, Club.class);

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
