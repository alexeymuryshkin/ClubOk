package dc.clubok.db.controllers;

import dc.clubok.db.models.Club;
import dc.clubok.utils.exceptions.ClubOkException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static dc.clubok.utils.Constants.model;

public class ClubController {
    private static Logger logger = LoggerFactory.getLogger(ClubController.class.getCanonicalName());

    public static void createClub(Club club) throws ClubOkException {
        model.saveOne(club, Club.class);
    }

    public static List<Club> getClubs(String params) throws ClubOkException {
//        TODO
        return null;
    }

    public static Club getClubById(String clubId) throws ClubOkException {
//        TODO
        return null;
    }

    public static List<ObjectId> getSubscribersByClubId(String clubId) throws ClubOkException {
//        TODO
        return null;
    }

    public static void deleteSubscriber(String clubId, String userId) {
//        TODO
    }

    public static List<ObjectId> getModeratorsByClubId(String clubId) throws ClubOkException {
//        TODO
        return null;
    }

    public static void deleteModerator(String clubId, String userId) throws ClubOkException {
//        TODO
    }

    public static List<ObjectId> getParticipantsByClubId(String clubId) throws ClubOkException {
//        TODO
        return null;
    }

    public static void deleteParticipant(String clubId, String userId) throws ClubOkException {
//        TODO
    }

    public static void deleteClubById(String clubId) throws ClubOkException {
//        TODO
    }

    public static void editClub(String clubId, Document update) throws ClubOkException {
//        TODO
    }
}
