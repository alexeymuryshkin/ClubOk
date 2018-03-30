package dc.clubok.db.controllers;

import dc.clubok.db.models.Club;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.List;

import static dc.clubok.utils.Constants.model;

public class ClubController {
    private static Logger logger = LoggerFactory.getLogger(ClubController.class.getCanonicalName());

    public static void createClub(Club club) throws Exception {
        model.saveOne(club, Club.class);
    }

    public static List<Club> getClubs(Request request, Response response) {
//        TODO
        return null;
    }

    public static Club getClubById(Request request, Response response) {
//        TODO
        return null;
    }

    public static List<ObjectId> getSubscribersByClubId(Request request, Response response) {
//        TODO
        return null;
    }

    public static List<ObjectId> getModeratorsByClubId(Request request, Response response) {
//        TODO
        return null;
    }

    public static List<ObjectId> getParticipantsByClubId(Request request, Response response) {
//        TODO
        return null;
    }

    public static void deleteClubById(Request request, Response response) {
//        TODO
    }

    public static void editClubInfoById(Request request, Response response) {
//        TODO
    }

}
