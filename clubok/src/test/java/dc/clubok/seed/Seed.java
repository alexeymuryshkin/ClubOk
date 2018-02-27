package dc.clubok.seed;

import dc.clubok.models.Club;
import dc.clubok.mongomodel.MongoModel;
import dc.clubok.models.Token;
import dc.clubok.models.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Seed {
    public static List<User> users;
    public static List<Club> clubs;

    public static void populateUsers() {
        User user1 = new User("userOneEmail@example.com", "userOnePass");
        User user2 = new User("userTwoEmail@example.com", "userTwoPass");
        users = Arrays.asList(user1, user2);

        user1.setTokens(Collections.singletonList(new Token("auth", MongoModel.generateAuthToken(user1))));
        user2.setTokens(Collections.singletonList(new Token("auth", MongoModel.generateAuthToken(user2))));

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
}
