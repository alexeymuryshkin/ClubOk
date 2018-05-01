package dc.clubok.seed;

import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.Club;
import dc.clubok.db.models.Event;
import dc.clubok.db.models.Post;
import dc.clubok.db.models.User;

import java.util.*;

import static dc.clubok.utils.Constants.model;

public class Seed {
    public static List<User> users;
    public static List<Club> clubs;
    public static List<Post> posts;
    public static List<Event> events;

    public static void populateUsers() {
        User user1 = new User("userOneEmail@example.com", "userOnePass");
        User user2 = new User("userTwoEmail@example.com", "userTwoPass");
        users = Arrays.asList(user1, user2);

        try {
            user1.setTokens(Collections.singletonList(UserController.generateAuthToken(user1)));
            user2.setTokens(Collections.singletonList(UserController.generateAuthToken(user2)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            model.saveMany(users, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void populateClubs() {
        Club club1 = new Club("Club Name 1", "some description");
        Club club2 = new Club("Club Name 2", "some description");
        clubs = Arrays.asList(club1, club2);

        try {
            model.saveMany(clubs, Club.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void populatePosts() {
        Post post1 = new Post(clubs.get(0).getId(), "type", "Hello everyone! Goodbye!");
        post1.setUserId(users.get(0).getId());
        Post post2 = new Post(clubs.get(0).getId(), "Type", "Body");
        post2.setUserId(users.get(1).getId());
        posts = Arrays.asList(post1, post2);

        try {
            model.saveMany(posts, Post.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void populateEvents(){
        populateClubs();
        Date s = new GregorianCalendar(2018, 11, 1).getTime();
        Event event1 = new Event(clubs.get(0).getId(), "Party", "relax time",
                (int) System.currentTimeMillis() / 1000, (int) System.currentTimeMillis() / 1000);
        Event event2 = new Event(clubs.get(0).getId(), "Party", "relax time",
                (int) System.currentTimeMillis() / 1000, (int) System.currentTimeMillis() / 1000);
        Event event3 = new Event(clubs.get(0).getId(), "Party", "relax time",
                (int) System.currentTimeMillis() / 1000, (int) System.currentTimeMillis() / 1000);
        Event event4 = new Event(clubs.get(0).getId(), "name", "description",
                (int) System.currentTimeMillis() / 1000, (int) System.currentTimeMillis() / 1000);
        Event event5 = new Event(clubs.get(0).getId(), "Party", "relax time",
                (int) System.currentTimeMillis() / 1000, (int) System.currentTimeMillis() / 1000);
        Event event6 = new Event(clubs.get(0).getId(), "Party", "relax time",
                (int) System.currentTimeMillis() / 1000, (int) System.currentTimeMillis() / 1000);
        events = Arrays.asList(event1, event2, event3, event4, event5, event6);

        try {
            model.saveMany(events, Event.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
