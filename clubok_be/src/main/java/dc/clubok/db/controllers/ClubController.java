package dc.clubok.db.controllers;

import dc.clubok.db.models.Club;
import dc.clubok.utils.exceptions.ClubOkException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Updates.set;
import static dc.clubok.utils.Constants.model;

public class ClubController {
    private static Logger logger = LoggerFactory.getLogger(ClubController.class.getCanonicalName());

    public static void createClub(Club club) throws ClubOkException {
        model.saveOne(club, Club.class);
    }

    public static List<Club> getClubs(String params) throws ClubOkException {
//        TODO add params handling
        return model.findAll(Club.class);
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
        Set<ObjectId> subscribers = club.getSubscribers();
        subscribers.remove(new ObjectId(userId));
        model.update(club, set("subscribers", subscribers), Club.class);
    }

    public static Set<ObjectId> getModeratorsByClubId(String clubId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);
        return club.getModerators();
    }

    public static void addModerator(String clubId, String userId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);
        Set<ObjectId> moderators = club.getModerators();
        moderators.add(new ObjectId(userId));
        model.update(club, set("moderators", moderators), Club.class);
    }

    public static void deleteModerator(String clubId, String userId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);
        Set<ObjectId> moderators = club.getModerators();
        moderators.remove(new ObjectId(userId));
        model.update(club, set("moderators", moderators), Club.class);
    }

    public static Set<ObjectId> getParticipantsByClubId(String clubId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);
        return club.getParticipants();
    }

    public static void addParticipant(String clubId, String userId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);
        Set<ObjectId> participants = club.getParticipants();
        participants.add(new ObjectId(userId));
        model.update(club, set("participants", participants), Club.class);
    }

    public static void deleteParticipant(String clubId, String userId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);
        Set<ObjectId> participants = club.getParticipants();
        participants.remove(new ObjectId(userId));
        model.update(club, set("participants", participants), Club.class);
    }

    public static void deleteClubById(String clubId) throws ClubOkException {
        model.deleteById(clubId, Club.class);
    }

    public static void editClub(String clubId, Document update) throws ClubOkException {
//        TODO
    }

    public static Club findByName(String name) throws ClubOkException {
        return model.findByField("name", name, Club.class);
    }
}
