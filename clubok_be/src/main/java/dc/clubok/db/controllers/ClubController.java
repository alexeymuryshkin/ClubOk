package dc.clubok.db.controllers;

import dc.clubok.db.models.Club;
import dc.clubok.db.models.Membership;
import dc.clubok.utils.ClubOkException;
import dc.clubok.utils.SearchParams;
import org.bson.Document;

import java.util.List;
import java.util.Set;

import static dc.clubok.utils.Constants.ERROR_QUERY;
import static dc.clubok.utils.Constants.model;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

public class ClubController {
    public static void createClub(Club club) throws ClubOkException {
        model.saveOne(club, Club.class);
    }

    public static void createManyClubs(List<Club> clubs) throws ClubOkException {
        model.saveMany(clubs, Club.class);
    }

    public static List<Club> getClubs(SearchParams params) throws ClubOkException {
        return model.findByParams(params, Club.class);
    }

    public static Club getClubById(String clubId) throws ClubOkException {
        return model.findById(clubId, Club.class);
    }

    public static Club getClubByName(String name) throws ClubOkException {
        return model.findByField("name", name, Club.class);
    }

    public static Club getClubByLink(String link) throws ClubOkException {
        return model.findByField("link", link, Club.class);
    }

    public static Set<String> getSubscribersByClubId(String clubId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);
        if (club == null) {
            Document details = new Document("details", "Such club does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }

        return club.getSubscribers();
    }

    public static void addSubscriber(String clubId, String userId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);
        if (club == null) {
            Document details = new Document("details", "Such club does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }

        model.addOneToSet(club, "subscribers", userId, Club.class);
    }

    public static void deleteSubscriber(String clubId, String userId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);
        if (club == null) {
            Document details = new Document("details", "Such club does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }

        model.removeOneFromArray(club, "subscribers", userId, Club.class);
    }

    public static Set<Membership> getMembersByClubId(String clubId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);
        if (club == null) {
            Document details = new Document("details", "Such club does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }

        return club.getMembers();
    }

    public static void addMember(String clubId, String userId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);
        if (club == null) {
            Document details = new Document("details", "Such club does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }

        model.addOneToSet(club, "members", userId, Club.class);
    }

    public static void deleteMember(String clubId, String userId) throws ClubOkException {
        Club club = model.findById(clubId, Club.class);
        if (club == null) {
            Document details = new Document("details", "Such club does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }

        model.removeOneFromArray(club, "members", userId, Club.class);
    }

    public static void deleteClubById(String clubId) throws ClubOkException {
        Club club = getClubById(clubId);
        if (club == null) {
            Document details = new Document("details", "Such club does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }

        model.deleteById(clubId, Club.class);
    }

    public static void editClub(String clubId, Document update) throws ClubOkException {
        Club club = getClubById(clubId);
        if (club == null) {
            Document details = new Document("details", "Such club does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }

        model.modify(club, update, Club.class);
    }
}
