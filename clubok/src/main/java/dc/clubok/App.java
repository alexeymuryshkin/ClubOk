package dc.clubok;

import com.google.gson.Gson;
import dc.clubok.config.Config;
import dc.clubok.data.Crypt;
import dc.clubok.db.MongoHandle;
import dc.clubok.models.User;
import dc.clubok.data.MongoUserDB;
import org.bson.Document;
import org.bson.types.ObjectId;

import static spark.Spark.*;

public class App {
    public final static Config config = new Config();
    public final static MongoHandle mongo = new MongoHandle();

    public static void main(String[] args) {
        port(Integer.valueOf(config.getProperties().getProperty("port")));
        System.out.println("Server started at port " + config.getProperties().getProperty("port"));

        final Gson gson = new Gson();

        post("/users", "application/json", (req, res) -> {
            System.out.println("POST /users\n" + req.body());
            User user = gson.fromJson(req.body(), User.class);
            res.type("application/json");

            user.setId(new ObjectId());
            user.setPassword(Crypt.hash(user.getPassword().toCharArray()));

            try {
                String authToken = MongoUserDB.generateAuthToken(user);
                MongoUserDB.save(user);
                res.header("x-auth", authToken);
                return user;
            } catch (Exception e){
                System.out.println(e.getMessage());
                return "";
            }
        }, gson ::toJson);

        post("/users/login", "application/json", (req, res) -> {
            System.out.println("POST /users/login\n" + req.body());
            res.type("application/json");

            try {
                User user = MongoUserDB.findByCredentials(
                        gson.fromJson(req.body(), User.class).getEmail(),
                        gson.fromJson(req.body(), User.class).getPassword()
                );

                res.header("x-auth", MongoUserDB.generateAuthToken(user));
                MongoUserDB.update(user, new Document("tokens", user.getTokens()));
                res.type("application/json");
                return user;
            } catch (NullPointerException e) {
                res.status(400);
                return "";
            }
        }, gson ::toJson);
    }
}
