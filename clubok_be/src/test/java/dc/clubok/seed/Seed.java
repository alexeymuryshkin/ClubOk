package dc.clubok.seed;

import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.Club;
import dc.clubok.db.models.Post;
import dc.clubok.db.models.User;
import dc.clubok.db.mongomodel.MongoModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Seed {
    public static List<User> users;
    public static List<Club> clubs;
    public static List<Post> posts;

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
            new MongoModel().saveMany(users, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void populateClubs() {
        Club club1 = new Club("Club Name 1");
        Club club2 = new Club("Club Name 2");
        clubs = Arrays.asList(club1, club2);

        try {
            new MongoModel().saveMany(clubs, Club.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void populatePosts() {
        populateUsers();
        populateClubs();
        Post post1 = new Post(clubs.get(0).getId().toHexString(), "type", "Buffalo", "Hello everyone! Goodbye!");
        Post post2 = new Post(clubs.get(0).getId().toHexString(), "Type", "Title", "Body");
        posts = Arrays.asList(post1, post2);

        try {
            new MongoModel().saveMany(posts, Post.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
