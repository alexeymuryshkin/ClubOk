package dc.clubok.seed;

import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.Club;
import dc.clubok.db.models.Event;
import dc.clubok.db.models.Post;
import dc.clubok.db.models.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static dc.clubok.utils.Constants.PL_ADMINISTRATOR;
import static dc.clubok.utils.Constants.model;

public class Seed {
    public static List<User> users;
    public static List<Club> clubs;
    public static List<Post> posts;
    public static List<Event> events;

    public static void populateUsers() {
        User admin = new User("admin@clubok.kz", "administrator", PL_ADMINISTRATOR);
        User user1 = new User("userOneEmail@example.com", "userOnePass");
        User user2 = new User("userTwoEmail@example.com", "userTwoPass");
        users = Arrays.asList(admin, user1, user2);

        try {
            admin.setTokens(Collections.singletonList(UserController.generateAuthToken(admin)));
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
        Post post1 = new Post(clubs.get(0), users.get(0), "regular", "Hello everyone! Goodbye!");
        Post post2 = new Post(clubs.get(0), users.get(1), "regular", "Body");
        posts = Arrays.asList(post1, post2);

        try {
            model.saveMany(posts, Post.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void populateEvents(){
        populateClubs();
        Event event1 = new Event(clubs.get(0), "Party", "relax time",
                (int) System.currentTimeMillis() / 1000, (int) System.currentTimeMillis() / 1000);
        Event event2 = new Event(clubs.get(1), "Party", "relax time",
                (int) System.currentTimeMillis() / 1000, (int) System.currentTimeMillis() / 1000);
        Event event3 = new Event(clubs.get(0), "Party", "relax time",
                (int) System.currentTimeMillis() / 1000, (int) System.currentTimeMillis() / 1000);
        events = Arrays.asList(event1, event2, event3);

        try {
            model.saveMany(events, Event.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
