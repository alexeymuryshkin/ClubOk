package dc.clubok.seed;

import dc.clubok.Crypt;
import dc.clubok.mongomodel.MongoUserModel;
import dc.clubok.entities.Token;
import dc.clubok.entities.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Seed {
    public static List<User> users;

    public static void populateUsers() {
        User user1 = new User("userOneEmail@example.com", "userOnePass");
        User user2 = new User("userTwoEmail@example.com", "userTwoPass");
        users = Arrays.asList(user1, user2);

        user1.setTokens(Collections.singletonList(new Token("auth", MongoUserModel.generateAuthToken(user1))));
        user2.setTokens(Collections.singletonList(new Token("auth", MongoUserModel.generateAuthToken(user2))));

        try {
            new MongoUserModel().saveMany(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
