package dc.clubok;

import com.google.gson.Gson;
import dc.clubok.config.Config;
import dc.clubok.models.UserEntity;
import dc.clubok.data.MongoUserDB;

import static spark.Spark.*;

public class App {
    public static Config config = new Config();

    public static void main(String[] args) {
        port(Integer.valueOf(config.getProperties().getProperty("port")));
        System.out.println("Server started at port " + config.getProperties().getProperty("port"));

        MongoUserDB userDB = new MongoUserDB();

        final Gson gson = new Gson();

        post("/users", "application/json", (req, res) -> {
            System.out.println("POST /users\n" + req.body());
            UserEntity user = gson.fromJson(req.body(), UserEntity.class);
            userDB.add(user);

            res.type("application/json");
            return user;
        }, gson ::toJson);

        post("/users/login", "application/json", (req, res) -> {
            System.out.println("POST /users/login\n" + req.body());
            res.type("application/json");

            try {
                UserEntity user = userDB.findByCredentials(
                        gson.fromJson(req.body(), UserEntity.class).getEmail(),
                        gson.fromJson(req.body(), UserEntity.class).getPassword()
                );

//                TODO Add token generation and return as header

                return user;

            } catch (NullPointerException | IllegalArgumentException e) {
                res.status(400);
                return "";
            }
        }, gson ::toJson);
    }
}
