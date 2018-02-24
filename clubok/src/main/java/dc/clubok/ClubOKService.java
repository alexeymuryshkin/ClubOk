package dc.clubok;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dc.clubok.config.Config;
import dc.clubok.db.MongoHandle;
import dc.clubok.models.Club;
import dc.clubok.models.User;
import dc.clubok.models.Model;
import dc.clubok.mongomodel.MongoModel;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.Validator;

import static spark.Spark.*;

public class ClubOKService {
    public final static Config config = new Config();
    public final static MongoHandle mongo = new MongoHandle();
    public static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static Logger logger = LoggerFactory.getLogger(ClubOKService.class.getCanonicalName());
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        port(Integer.valueOf(config.getProperties().getProperty("port")));

        logger.info("Server started at port " + config.getProperties().getProperty("port"));

        Model model = new MongoModel();

        path("/api", () -> {
            path("/users", () -> {
                post("", "application/json", (req, res) -> {
                    logger.debug("POST /users " + req.body());
                    try {
                        User user = gson.fromJson(req.body(), User.class);

                        res.header("x-auth", MongoModel.generateAuthToken(user));
                        model.save(user, User.class);

                        res.status(200);
                        res.type("application/json");
                        return user;
                    } catch (Exception e) {
                        res.status(400);
                        return "";
                    }

                }, gson::toJson);

                before("/me", ((req, res) -> {
                    if(!model.authenticate(req, res))
                        throw halt(401);
                }));
                get("/me", (req, res) -> {
                    logger.debug("GET /users/me " + req.headers("x-auth"));
                    res.type("application/json");
                    return model.findByToken(req.headers("x-auth"));
                }, gson::toJson);

                post("/login", "application/json", (req, res) -> {
                    logger.debug("POST /users/login " + req.body());
                    res.type("application/json");

                    try {
                        User user = model.findByCredentials(
                                gson.fromJson(req.body(), User.class).getEmail(),
                                gson.fromJson(req.body(), User.class).getPassword()
                        );

                        res.header("x-auth", MongoModel.generateAuthToken(user));
                        model.update(user, new Document("tokens", user.getTokens()), User.class);
                        res.type("application/json");
                        return user;
                    } catch (NullPointerException e) {
                        res.status(400);
                        return "";
                    }
                }, gson::toJson);

                before("/me/token", (req, res) -> {
                    if (!model.authenticate(req, res))
                        throw halt(401);
                });
                delete("/me/token", (req, res) -> {
                    logger.debug("DELETE /users " + req.headers("x-auth"));
                    res.type("application/json");
                    User user = model.findByToken(req.headers("x-auth"));
                    model.removeToken(user, req.headers("x-auth"));
                    res.status(200);
                    return user;
                }, gson::toJson);
            });

            path("/clubs", () -> {
                post("", "application/json", (req, res) -> {
                    try {
                        Club club = gson.fromJson(req.body(), Club.class);

                        model.save(club, Club.class);
                        res.type("application/json");
                        res.status(200);

                        return club;
                    } catch (Exception e) {
                        res.status(400);
                        return "";
                    }
                }, gson::toJson);

            });
        });


    }
}
