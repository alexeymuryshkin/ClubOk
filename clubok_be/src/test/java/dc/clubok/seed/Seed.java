package dc.clubok.seed;

import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.Club;
import dc.clubok.db.models.Post;
import dc.clubok.db.models.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static dc.clubok.utils.Constants.model;

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
            model.saveMany(users, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void populateClubs() {
        Club club1 = new Club("Club Name 1");
        Club club2 = new Club("Club Name 2");
        clubs = Arrays.asList(club1, club2);

        try {
            model.saveMany(clubs, Club.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void populatePosts() {
        Post post1 = new Post(clubs.get(0).getId().toHexString(), "type", "Buffalo", "Hello everyone! Goodbye!");
        post1.setUserId(users.get(0).getId().toHexString());
        Post post2 = new Post(clubs.get(0).getId().toHexString(), "Type", "Title", "Body");
        post2.setUserId(users.get(1).getId().toHexString());
        posts = Arrays.asList(post1, post2);

        try {
            model.saveMany(posts, Post.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
