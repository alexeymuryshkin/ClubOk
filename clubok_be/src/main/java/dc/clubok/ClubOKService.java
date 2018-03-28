package dc.clubok;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dc.clubok.config.Config;
import dc.clubok.controllers.ClubController;
import dc.clubok.controllers.PostController;
import dc.clubok.controllers.UserController;
import dc.clubok.db.MongoHandle;
import dc.clubok.models.Club;
import dc.clubok.models.Event;
import dc.clubok.models.User;
import dc.clubok.models.Model;
import dc.clubok.mongomodel.MongoModel;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.Validator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

public class ClubOKService {
    public final static Config config = new Config();
    public final static MongoHandle mongo = new MongoHandle();
    public static final Model model = new MongoModel();
    public static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static Logger logger = LoggerFactory.getLogger(ClubOKService.class.getCanonicalName());
    private static final Gson gson = new Gson();

    private static final String JSON = "application/json";

    public static void main(String[] args) {
        port(Integer.valueOf(config.getProperties().getProperty("port")));
        staticFiles.location("/public");

        logger.info("Server started at port " + config.getProperties().getProperty("port"));

        path("/api", () -> {
            path("/users", () -> {
                get("", UserController.fetchAllUsers, gson::toJson);
                post("", JSON, UserController.signUp, gson::toJson);
                post("/login", JSON, UserController.login, gson::toJson);

                path("/me", () -> {
                    before("", (request, response) -> {
                        if (!model.authenticate(request, response)) {
                            throw halt(401);
                        }
                    });
                    before("/*", (request, response) -> {
                        if (!model.authenticate(request, response)) {
                            throw halt(401);
                        }
                    });

                    get("", UserController.getPersonalInfo, gson::toJson);
                    delete("/token", UserController.logout, gson::toJson);
                    delete("/token/all", UserController.logoutAll, gson::toJson);
                });
                path("/:id", () -> {
                    get("", UserController.getUserById, gson::toJson);
                    get("/subscriptions", UserController.getSubscriptionsByUserId, gson::toJson);
                    get("/tokens", UserController.getTokensByUserId, gson::toJson);
                    delete("", UserController.deleteUserById, gson::toJson);
                });
            });

            path("/clubs", () -> {
                before("", (request, response) -> {
                    if (!model.authenticate(request, response))
                        throw halt(401);
                });
                get("", ClubController.fetchAllClubs, gson::toJson);
                post("", JSON, ClubController.createClub, gson::toJson);

                path("/:id", () -> {
                    get("", ClubController.getClubById, gson::toJson);
                    get("/subscribers", ClubController.getSubscribersByClubId, gson::toJson);
                    get("/participants", ClubController.getParticipantsByClubId, gson::toJson);
                    get("/moderators", ClubController.getModeratorsByClubId, gson::toJson);
                    delete("", ClubController.deleteClubById, gson::toJson);
                    patch("", ClubController.editClubInfoById, gson::toJson);
                });
            });

            path("/posts", () -> {
                before("", (request, response) -> {
                    if (!model.authenticate(request, response))
                        throw halt(401);
                });
                before("/*", (request, response) -> {
                    if (!model.authenticate(request, response))
                        throw halt(401);
                });
                get("", PostController.fetchAllPosts, gson::toJson);
                post("", JSON, PostController.createPost, gson::toJson);

                path("/:id", () -> {
                    get("", PostController.getPostById, gson::toJson);
                    get("/comments", PostController.getCommentsByPostId, gson::toJson);
                    post("/comments", JSON, PostController.addCommentToPost, gson::toJson);
                    get("/likes", PostController.getLikesByPostId, gson::toJson);
                    post("/likes", PostController.addLikeToPost, gson::toJson);
                    delete("", PostController.deletePostById, gson::toJson);
                    patch("", PostController.editPostById, gson::toJson);
                });
            });

            path("/events", () -> {
                post("/update", "application/json", (request, response) -> {
                    try {
                        Event event = gson.fromJson(request.body(), Event.class);
                        if (model.findById(event.getId(), Event.class) != null){
                            model.update(event, new Document().append("datetime", event.getDatetime()), Event.class);
                            model.update(event, new Document().append("description", event.getDescription()), Event.class);
                            model.update(event, new Document().append("title", event.getTitle()), Event.class);
                            response.type("application/json");
                            response.status(200);
                            return event;
                        }else{
                            response.status(404);
                            return "";
                        }
                    } catch (Exception e) {
                        response.status(400);
                        return "";
                    }
                }, gson::toJson);
                post("/register", "application/json", (request, response) -> {
                    try {
                        Event event = gson.fromJson(request.body(), Event.class);
                        model.save(event, Event.class);
                        response.type("application/json");
                        response.status(200);
                        return event;
                    } catch (Exception e) {
                        response.status(400);
                        return "";
                    }
                }, gson::toJson);
            });

            path("/subscriptions", () -> {
                before("", (request, response) -> {
                    if (!model.authenticate(request, response))
                        throw halt(401);
                });
                System.out.println();
                post("/subscribe", "application/json", (request, response) -> {
                    Type listType = new TypeToken<ArrayList<ObjectId>>(){}.getType();
                    List<ObjectId> a = gson.fromJson(request.body(), listType);
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
                            response.type("application/json");
                            response.status(200);
                            return "works";
                        } catch (Exception e) {
                            response.status(400);
                            return "";
                        }
                    }else{
                        response.status(302);
                        return "";
                    }
                }, gson::toJson);

                post("/unsubscribe", "application/json", (request, response) -> {
                    Type listType = new TypeToken<ArrayList<ObjectId>>(){}.getType();
                    List<ObjectId> a = gson.fromJson(request.body(), listType);
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
                            response.type("application/json");
                            response.status(200);
                            return "works";
                        } catch (Exception e) {
                            response.status(400);
                            return "";
                        }
                    }else{
                        response.status(404);
                        return "";
                    }
                }, gson::toJson);
            });
        });


    }
}
