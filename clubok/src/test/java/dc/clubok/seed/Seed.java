package dc.clubok.seed;

import dc.clubok.Crypt;
import dc.clubok.mongomodel.MongoUserModel;
import dc.clubok.models.Token;
import dc.clubok.models.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Seed {
    public static List<User> users;

    public static void populateUsers() {
        User user1 = new User("userOneEmail@example.com", "userOnePass");
        User user2 = new User("userTwoEmail@example.com", "userTwoPass");
        users = Arrays.asList(user1, user2);

        User user1Db = new User(user1.getEmail(), Crypt.hash(user1.getPassword().toCharArray()));
        user1Db.setId(user1.getId());
        User user2Db = new User(user2.getEmail(), Crypt.hash(user2.getPassword().toCharArray()));
        user2Db.setId(user2.getId());
        List<User> usersDb = Arrays.asList(user1Db, user2Db);

        user1.setTokens(Collections.singletonList(new Token("auth", MongoUserModel.generateAuthToken(user1Db))));
        user2.setTokens(Collections.singletonList(new Token("auth", MongoUserModel.generateAuthToken(user2Db))));

        user1Db.setTokens(Collections.singletonList(new Token("auth", MongoUserModel.generateAuthToken(user1Db))));
        user2Db.setTokens(Collections.singletonList(new Token("auth", MongoUserModel.generateAuthToken(user2Db))));

        try {
            new MongoUserModel().saveMany(usersDb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
