package dc.clubok.db.controllers;

import dc.clubok.db.models.Club;
import dc.clubok.utils.exceptions.ClubOkException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Set;

import static dc.clubok.utils.Constants.CLUB_NOT_FOUND;
import static dc.clubok.utils.Constants.model;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

public class ClubController {
    public static void createClub(Club club) throws ClubOkException {
        model.saveOne(club, Club.class);
    }

    public static List<Club> getClubs(int size, int page, String orderBy, String order, Bson include, Bson exclude) throws ClubOkException {
        return model.findMany(size, page, orderBy, order, include, exclude, Club.class);
    }

    public static Club getClubById(String clubId) throws ClubOkException {
        return model.findById(clubId, Club.class);
    }

    public static Set<ObjectId> getSubscribersByClubId(String clubId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);
        return club.getSubscribers();
    }

    public static void addSubscriber(String clubId, String userId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);

        model.addOneToSet(club, "subscribers", userId, Club.class);
    }

    public static void deleteSubscriber(String clubId, String userId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);
        model.removeOneFromArray(club, "subscribers", userId, Club.class);
    }

    public static Set<ObjectId> getModeratorsByClubId(String clubId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);
        return club.getModerators();
    }

    public static void addModerator(String clubId, String userId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);

        model.addOneToSet(club, "moderators", userId, Club.class);
    }

    public static void deleteModerator(String clubId, String userId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);

        model.removeOneFromArray(club, "moderators", userId, Club.class);
    }

    public static Set<ObjectId> getMembersByClubId(String clubId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);
        return club.getMembers();
    }

    public static void addMember(String clubId, String userId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);

        model.addOneToSet(club, "members", userId, Club.class);
    }

    public static void deleteParticipant(String clubId, String userId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);

        model.removeOneFromArray(club, "members", userId, Club.class);
    }

    public static void deleteClubById(String clubId) throws ClubOkException {
        Club club = getClubById(clubId);
        if (club == null) {
            throw new ClubOkException(CLUB_NOT_FOUND, "Club does not exist", SC_NOT_FOUND);
        }
        model.deleteById(clubId, Club.class);
    }

    public static void editClub(String clubId, Document update) throws ClubOkException {
        Club club = getClubById(clubId);
        if (club == null) {
            throw new ClubOkException(CLUB_NOT_FOUND, "Club does not exist", SC_NOT_FOUND);
        }
        model.modify(club, update, Club.class);
    }

    public static Club findByName(String name) throws ClubOkException {
        return model.findByField("name", name, Club.class);
    }
}
