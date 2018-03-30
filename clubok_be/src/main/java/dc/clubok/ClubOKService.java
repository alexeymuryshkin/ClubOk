package dc.clubok;

import com.google.gson.reflect.TypeToken;
import dc.clubok.db.models.Club;
import dc.clubok.db.models.Event;
import dc.clubok.db.models.Post;
import dc.clubok.db.models.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static dc.clubok.routes.ClubRoute.*;
import static dc.clubok.routes.EventRoute.*;
import static dc.clubok.routes.PostRoute.*;
import static dc.clubok.routes.UserRoute.*;
import static dc.clubok.utils.Constants.*;
import static spark.Spark.*;

public class ClubOKService {
    private static Logger logger = LoggerFactory.getLogger(ClubOKService.class.getCanonicalName());
    public static Map<Session, String> clients = new ConcurrentHashMap<>();
    public static int nextUserNumber = 1;

    public static void main(String[] args) {
        port(Integer.valueOf(config.getProperties().getProperty("port")));
        staticFiles.location("/public");
        logger.info("Server started at port " + config.getProperties().getProperty("port"));

        webSocket("/feed", FeedWebSocketHandler.class);
        path("/api", () -> {
            path("/users", () -> {
                get("", GetUsers, gson::toJson);
                post("", JSON, PostUsers, gson::toJson);
                post("/login", JSON, PostUsersLogin, gson::toJson);

                path("/me", () -> {
                    before("", (request, response) -> {
                        if (notAuthenticated(request, response)) {
                            throw halt(401);
                        }
                    });
                    before("/*", (request, response) -> {
                        if (notAuthenticated(request, response)) {
                            throw halt(401);
                        }
                    });

                    get("", GetUsersMe, gson::toJson);
                    get("/subscriptions", GetUsersMeSubscriptions, gson::toJson);
                    get("/tokens", GetUsersMeTokens, gson::toJson);
                    delete("/tokens", DeleteUsersMeTokens, gson::toJson);
                    delete("/token", DeleteUsersMeToken, gson::toJson);
                });
                path("/:id", () -> {
                    get("", GetUsersId, gson::toJson);
                    get("/subscriptions", GetUsersIdSubscriptions, gson::toJson);
                    get("/tokens", GetUsersIdTokens, gson::toJson);
                    delete("", DeleteUsersId, gson::toJson);
                });
            });

            path("/clubs", () -> {
                before("", (request, response) -> {
                    if (notAuthenticated(request, response))
                        throw halt(401);
                });
                get("", GetClubs, gson::toJson);
                post("", JSON, PostClubs, gson::toJson);

                path("/:id", () -> {
                    get("", GetClubsId, gson::toJson);
                    get("/subscribers", GetClubsIdSubscribers, gson::toJson);
                    get("/participants", GetClubsIdParticipants, gson::toJson);
                    get("/moderators", GetClubsIdModerators, gson::toJson);
                    delete("", DeleteClubsId, gson::toJson);
                    patch("", JSON, PatchClubsId, gson::toJson);
                });
            });

            path("/posts", () -> {
                before("", (request, response) -> {
                    if (notAuthenticated(request, response))
                        throw halt(401);
                });
                before("/*", (request, response) -> {
                    if (notAuthenticated(request, response))
                        throw halt(401);
                });
                get("", GetPosts, gson::toJson);
                post("", JSON, PostPosts, gson::toJson);

                path("/:id", () -> {
                    get("", GetPostsId, gson::toJson);
                    get("/comments", GetPostsIdComments, gson::toJson);
                    post("/comments", PostPostsIdComments, gson::toJson);
                    get("/likes", GetPostsIdLikes, gson::toJson);
                    post("/likes", PostPostsIdLikes, gson::toJson);
                    delete("", DeletePostsId, gson::toJson);
                    patch("", PatchPostsId, gson::toJson);
                });
            });

            path("/events", () -> {
                post("", JSON, PostEvents, gson::toJson);
                get("", GetEvents, gson::toJson);

                path("/:id", () -> {
                    get("", GetEventsId, gson::toJson);
                });


                post("/update", "application/json", (request, response) -> {
                    try {
                        Event event = gson.fromJson(request.body(), Event.class);
                        if (model.findById(event.getId(), Event.class) != null) {
                            model.update(event, new Document().append("datetime", event.getDatetime()), Event.class);
                            model.update(event, new Document().append("description", event.getDescription()), Event.class);
                            model.update(event, new Document().append("title", event.getTitle()), Event.class);
                            response.type("application/json");
                            response.status(200);
                            return event;
                        } else {
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
                        model.saveOne(event, Event.class);
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
                    if (notAuthenticated(request, response))
                        throw halt(401);
                });
                System.out.println();
                post("/subscribe", "application/json", (request, response) -> {
                    Type listType = new TypeToken<ArrayList<ObjectId>>() {
                    }.getType();
                    List<ObjectId> a = gson.fromJson(request.body(), listType);
                    ObjectId clubId = a.get(1);
                    ObjectId userId = a.get(0);
                    User user = model.findById(userId, User.class);
                    Club club = model.findById(clubId, Club.class);
                    ArrayList<ObjectId> UsersArray = new ArrayList(user.getSubscriptions());
                    ArrayList<ObjectId> ClubsArray = new ArrayList(club.getSubscribers());
                    if (!UsersArray.contains(clubId) &&
                            !ClubsArray.contains(userId)) {

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
                    } else {
                        response.status(302);
                        return "";
                    }
                }, gson::toJson);

                post("/unsubscribe", "application/json", (request, response) -> {
                    Type listType = new TypeToken<ArrayList<ObjectId>>() {
                    }.getType();
                    List<ObjectId> a = gson.fromJson(request.body(), listType);
                    ObjectId clubId = a.get(1);
                    ObjectId userId = a.get(0);
                    User user = model.findById(userId, User.class);
                    Club club = model.findById(clubId, Club.class);
                    ArrayList<ObjectId> UsersArray = new ArrayList(user.getSubscriptions());
                    ArrayList<ObjectId> ClubsArray = new ArrayList(club.getSubscribers());
                    if (UsersArray.contains(clubId) &&
                            ClubsArray.contains(userId)) {

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
                    } else {
                        response.status(404);
                        return "";
                    }
                }, gson::toJson);
            });

            path("/search", () -> {

            });
        });


        init();
    }

    public static void broadcastPost(Post post) {
        clients.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(gson.toJson(Collections.singletonList(post)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void sendPost(Post post) {

    }

    public static void sendPosts(Session user) {
        try {
            user.getRemote().sendString(gson.toJson(model.findAll(Post.class)));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
