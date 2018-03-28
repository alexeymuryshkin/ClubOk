package dc.clubok;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import dc.clubok.config.Config;
import dc.clubok.db.MongoHandle;
import dc.clubok.models.Club;
import dc.clubok.models.Event;
import dc.clubok.models.Post;
import dc.clubok.models.User;
import dc.clubok.models.Model;
import dc.clubok.mongomodel.MongoModel;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.Validator;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static spark.Spark.*;

public class ClubOKService {
    public final static Config config = new Config();
    public final static MongoHandle mongo = new MongoHandle();
    public static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static Logger logger = LoggerFactory.getLogger(ClubOKService.class.getCanonicalName());
    private static final Gson gson = new Gson();

    private static final String JSON = "application/json";

    public static void main(String[] args) {
        port(Integer.valueOf(config.getProperties().getProperty("port")));
        staticFiles.location("/public");

        logger.info("Server started at port " + config.getProperties().getProperty("port"));

        Model model = new MongoModel();

        path("/api", () -> {
            path("/users", () -> {
                post("", JSON, (req, res) -> {
                    logger.debug("POST /users " + req.body());
                    try {
                        User user = gson.fromJson(req.body(), User.class);

                        res.header("x-auth", MongoModel.generateAuthToken(user));
                        model.save(user, User.class);

                        res.status(201);
                        res.type(JSON);
                        return user;
                    } catch (Exception e) {
                        res.type(JSON);
                        res.status(400);
                        return e;
                    }

                }, gson::toJson);

                path("/me", () -> {
                    before("", (req, res) -> {
                        if (!model.authenticate(req, res)) {
                            throw halt(401);
                        }
                    });
                    before("/*", (req, res) -> {
                        if (!model.authenticate(req, res)) {
                            throw halt(401);
                        }
                    });
                    get("", (req, res) -> {
                        logger.debug("GET /users/me " + req.headers("x-auth"));

                        res.type(JSON);
                        res.status(200);
                        return model.findByToken(req.headers("x-auth"));
                    }, gson::toJson);

                    delete("/token", (req, res) -> {
                        logger.debug("DELETE /users/me/token " + req.headers("x-auth"));

                        User user = model.findByToken(req.headers("x-auth"));
                        model.removeToken(user, req.headers("x-auth"));

                        res.type(JSON);
                        res.status(204);
                        return "";
                    }, gson::toJson);

                    delete("/token/all", (req, res) -> {
                        logger.debug("DELETE /users/token/all");

                        User user = model.findByToken(req.headers("x-auth"));
                        model.removeAllTokens(user);

                        res.type(JSON);
                        res.status(204);
                        return "";
                    }, gson::toJson);
                });

                path("/:id", () -> {
                    get("", (req, res) -> {
                        logger.debug("GET /users/" + req.params(":id"));

                        try {
                            User user = model.findById(new ObjectId(req.params(":id")), User.class);
                            if (user == null) {
                                res.type(JSON);
                                res.status(404);
                                return "";
                            }

                            res.type(JSON);
                            res.status(200);
                            return user;
                        } catch (IllegalArgumentException e) {
                            res.type(JSON);
                            res.status(404);
                            return "";
                        } catch (Exception e) {
                            res.type(JSON);
                            res.status(400);
                            return e;
                        }
                    }, gson::toJson);
                });

                post("/login", JSON, (req, res) -> {
                    logger.debug("POST /users/login " + req.body());
                    try {
                        User user = model.findByCredentials(
                                gson.fromJson(req.body(), User.class).getEmail(),
                                gson.fromJson(req.body(), User.class).getPassword()
                        );

                        res.header("x-auth", MongoModel.generateAuthToken(user));
                        model.update(user, new Document("tokens", user.getTokens()), User.class);
                        res.type(JSON);
                        res.status(200);
                        return user;
                    } catch (NullPointerException e) {
                        res.type(JSON);
                        res.status(400);
                        return e;
                    }
                }, gson::toJson);
            });

            path("/clubs", () -> {
                before("", (req, res) -> {
                    if (!model.authenticate(req, res))
                        throw halt(401);
                });
                post("", JSON, (req, res) -> {
                    try {
                        Club club = gson.fromJson(req.body(), Club.class);
                        model.save(club, Club.class);

                        res.type(JSON);
                        res.status(200);
                        return club;
                    } catch (Exception e) {
                        res.type(JSON);
                        res.status(400);
                        return e;
                    }
                }, gson::toJson);
            });

            path("/comments", () -> {

            });

            path("/posts", () -> {
                before("*", (req, res) -> {
                    if (!model.authenticate(req, res))
                        throw halt(401);
                });
                post("", JSON, (req, res) -> {
                    logger.debug("POST /posts " + req.body());
                    try {
                        Post post = gson.fromJson(req.body(), Post.class);
                        post.setUserId(model.findByToken(req.headers("x-auth")).getId().toHexString());
                        model.save(post, Post.class);

                        res.type(JSON);
                        res.status(200);
                        return post;
                    } catch (Exception e) {
                        res.type(JSON);
                        res.status(400);
                        return e;
                    }
                }, gson::toJson);

                path("/:id", () -> {
                    get("", (req, res) -> {
                        try {
                            Post post = model.findById(new ObjectId(req.params(":id")), Post.class);
                            if (post != null) {
                                res.type(JSON);
                                res.status(200);
                                return post;
                            } else {
                                res.status(404);
                                return "";
                            }
                        } catch (Exception e) {
                            res.type(JSON);
                            res.status(400);
                            return e;
                        }
                    }, gson::toJson);
                });

                before("/club", ((req, res) -> {
                    logger.debug("Get /posts " + req.headers("x-auth"));
                    if(!model.authenticate(req, res))
                        throw halt(401);
                }));
                get("/club", (req, res) -> {
                    logger.debug("GET /posts/club " + req.headers("x-auth"));

                    ObjectId clubId = gson.fromJson(req.body(), ObjectId.class);
                    res.type("application/json");
                    return model.findByIdAll("clubId", clubId, Post.class);
                }, gson::toJson);

                before("/my", (req, res) -> {
                    if (!model.authenticate(req, res))
                        throw halt(401);
                });
//                get("", (req, res) -> {
//                    logger.debug("DELETE /posts/my " + req.headers("x-auth"));
//
//                    ObjectId postId = gson.fromJson(req.body(), ObjectId.class);
//                    res.type("application/json");
//                    User user = model.findByToken(req.headers("x-auth"));
//                    return model.findByUserAll(user, Post.class);
//                }, gson::toJson);

//                before("", (req, res) -> {
//                    if (!model.authenticate(req, res))
//                        throw halt(401);
//                });
                delete("", (req, res) -> {
                    logger.debug("DELETE /posts " + req.headers("x-auth"));
                    res.type("application/json");
                    ObjectId postId = gson.fromJson(req.body(), ObjectId.class);
                    model.removeById(postId, Post.class);
                    res.status(204);
                    return "";
                }, gson::toJson);
            });

            path("/events", () -> {
                post("/update", "application/json", (req, res) -> {
                    try {
                        Event event = gson.fromJson(req.body(), Event.class);
                        if (model.findById(event.getId(), Event.class) != null){
                            model.update(event, new Document().append("datetime", event.getDatetime()), Event.class);
                            model.update(event, new Document().append("description", event.getDescription()), Event.class);
                            model.update(event, new Document().append("title", event.getTitle()), Event.class);
                            res.type("application/json");
                            res.status(200);
                            return event;
                        }else{
                            res.status(404);
                            return "";
                        }
                    } catch (Exception e) {
                        res.status(400);
                        return "";
                    }
                }, gson::toJson);
                post("/register", "application/json", (req, res) -> {
                    try {
                        Event event = gson.fromJson(req.body(), Event.class);
                        model.save(event, Event.class);
                        res.type("application/json");
                        res.status(200);
                        return event;
                    } catch (Exception e) {
                        res.status(400);
                        return "";
                    }
                }, gson::toJson);
            });

            path("/subscriptions", () -> {
                before("", (req, res) -> {
                    if (!model.authenticate(req, res))
                        throw halt(401);
                });
                System.out.println();
                post("/subscribe", "application/json", (req, res) -> {
                    Type listType = new TypeToken<ArrayList<ObjectId>>(){}.getType();
                    List<ObjectId> a = gson.fromJson(req.body(), listType);
                    ObjectId clubId = a.get(1);
                    ObjectId userId = a.get(0);
                    User user = model.findById(userId, User.class);
                    Club club = model.findById(clubId, Club.class);
                    ArrayList<ObjectId> UsersArray = new ArrayList(user.getSubscriptions());
                    ArrayList<ObjectId> ClubsArray = new ArrayList(club.getSubscribers());
                    if (!UsersArray.contains(clubId) &&
                            !ClubsArray.contains(userId)){

                        UsersArray.add(clubId);
                        ClubsArray.add(userId);
                        try {
                            model.update(club, new Document().append("subscribers", ClubsArray), Club.class);
                            model.update(user, new Document().append("subscriptions", UsersArray), User.class);
                            res.type("application/json");
                            res.status(200);
                            return "works";
                        } catch (Exception e) {
                            res.status(400);
                            return "";
                        }
                    }else{
                        res.status(302);
                        return "";
                    }
                }, gson::toJson);

                post("/unsubscribe", "application/json", (req, res) -> {
                    Type listType = new TypeToken<ArrayList<ObjectId>>(){}.getType();
                    List<ObjectId> a = gson.fromJson(req.body(), listType);
                    ObjectId clubId = a.get(1);
                    ObjectId userId = a.get(0);
                    User user = model.findById(userId, User.class);
                    Club club = model.findById(clubId, Club.class);
                    ArrayList<ObjectId> UsersArray = new ArrayList(user.getSubscriptions());
                    ArrayList<ObjectId> ClubsArray = new ArrayList(club.getSubscribers());
                    if (UsersArray.contains(clubId) &&
                            ClubsArray.contains(userId)){

                        UsersArray.remove(clubId);
                        ClubsArray.remove(userId);
                        try {
                            model.update(club, new Document().append("subscribers", ClubsArray), Club.class);
                            model.update(user, new Document().append("subscriptions", UsersArray), User.class);
                            res.type("application/json");
                            res.status(200);
                            return "works";
                        } catch (Exception e) {
                            res.status(400);
                            return "";
                        }
                    }else{
                        res.status(404);
                        return "";
                    }
                }, gson::toJson);
            });
        });


    }
}
