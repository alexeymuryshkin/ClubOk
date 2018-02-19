package dc.clubok;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dc.clubok.config.Config;
import dc.clubok.data.Crypt;
import dc.clubok.data.MongoUserDB;
import dc.clubok.db.MongoHandle;
import dc.clubok.models.User;
import org.bson.Document;

import static spark.Spark.*;

public class App {
    public final static Config config = new Config();
    public final static MongoHandle mongo = new MongoHandle();

    public static void main(String[] args) {
        port(Integer.valueOf(config.getProperties().getProperty("port")));
        System.out.println("Server started at port " + config.getProperties().getProperty("port"));

        final Gson gson = new Gson();

        path("/users", () -> {
            post("", "application/json", (req, res) -> {
                System.out.println("POST /users\n" + req.body());
                try {
                    User user = gson.fromJson(req.body(), User.class);
//                if (!MongoUserDB.isValid(user)) {
//                    res.status(400);
//                    return "";
//                }

                    user.setPassword(Crypt.hash(user.getPassword().toCharArray()));
                    res.header("x-auth", MongoUserDB.generateAuthToken(user));
                    MongoUserDB.save(user);

                    res.status(200);
                    res.type("application/json");
                    return user;
                } catch (JsonParseException jpe) {
                    res.status(400);
                    return "";
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    res.status(400);
                    return "";
                }

            }, gson::toJson);

            get("/me", MongoUserDB::authenticate, gson::toJson);

            post("/login", "application/json", (req, res) -> {
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
            }, gson::toJson);

            delete("me/token", (req, res) -> {
                User user = MongoUserDB.authenticate(req, res);
                if (user == null)
                    throw halt(401);



                return "";
            }, gson::toJson);
        });


    }
}
