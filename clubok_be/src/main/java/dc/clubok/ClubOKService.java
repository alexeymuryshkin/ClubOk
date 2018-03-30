package dc.clubok;

import com.google.gson.reflect.TypeToken;
import dc.clubok.controllers.*;
import dc.clubok.models.Club;
import dc.clubok.models.Event;
import dc.clubok.models.Post;
import dc.clubok.models.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static dc.clubok.utils.Constants.*;
import static j2html.TagCreator.*;
import static spark.Spark.*;

public class ClubOKService {
    private static Logger logger = LoggerFactory.getLogger(ClubOKService.class.getCanonicalName());
    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    static int nextUserNumber = 1;

    public static void main(String[] args) {
        port(Integer.valueOf(config.getProperties().getProperty("port")));
        staticFiles.location("/public");
        webSocket("/feed", FeedWebSocketHandler.class);
        logger.info("Server started at port " + config.getProperties().getProperty("port"));

        path("/api", () -> {
            path("/users", () -> {
                get("", UserController.fetchAllUsers, gson::toJson);
                post("", JSON, UserController.signUp, gson::toJson);
                post("/login", JSON, UserController.login, gson::toJson);

                path("/me", () -> {
                    before("", (request, response) -> {
                        if (!UserController.authenticate(request, response)) {
                            throw halt(401);
                        }
                    });
                    before("/*", (request, response) -> {
                        if (!UserController.authenticate(request, response)) {
                            throw halt(401);
                        }
                    });

                    get("", UserController.getPersonalInfo, gson::toJson);
                    get("/subscriptions", UserController.getPersonalSubscriptions, gson::toJson);
                    get("/tokens", UserController.getPersonalTokens, gson::toJson);
                    delete("/tokens", UserController.logoutAll, gson::toJson);
                    delete("/token", UserController.logout, gson::toJson);
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
                    if (!UserController.authenticate(request, response))
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
                    if (!UserController.authenticate(request, response))
                        throw halt(401);
                });
                before("/*", (request, response) -> {
                    if (!UserController.authenticate(request, response))
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
                    delete("/likes", PostController.deleteLikesByPostId, gson::toJson);
                    delete("", PostController.deletePostById, gson::toJson);
                    patch("", PostController.editPostById, gson::toJson);
                    path("/comments", () -> {
                        patch("/:cid", PostController.editCommentByPostId, gson::toJson);
                        delete("/:cid", PostController.deleteCommentByPostId, gson::toJson);
                    });
                });
            });

            path("/events", () -> {
                post("", JSON, EventController.addEvent, gson::toJson);
                get("", EventController.fetchAllEvents, gson::toJson);

                path("/:id", () -> {
                    get("", EventController.getEventById, gson::toJson);
                    patch("" , EventController.editEventById, gson::toJson);
                    delete("", EventController.deleteEventById, gson::toJson);
                });

            });

            path("/subscriptions", () -> {
                before("", (request, response) -> {
                    if (!UserController.authenticate(request, response))
                        throw halt(401);
                });
                before("/*", (request, response) -> {
                    if (!UserController.authenticate(request, response))
                        throw halt(401);
                });

                post("", SubscriptionController.addSubscription, gson::toJson);
                delete("", SubscriptionController.deleteSubscription, gson::toJson);
            });
        });

        init();
    }

    public static void broadcastMessage(String sender, String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                logger.debug(String.valueOf(new JSONObject()
                        .put("userMessage", createHtmlMessageFromSender(sender, message))
                        .put("userlist", userUsernameMap.values())));
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("userMessage", createHtmlMessageFromSender(sender, message))
                        .put("userlist", userUsernameMap.values())
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void broadcastPost(Post post) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(gson.toJson(Collections.singletonList(post)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void sendPosts(Session user) {
        try {
            user.getRemote().sendString(gson.toJson(model.findAll(Post.class)));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static void sendMessage() {

    }

    private static String createHtmlMessageFromSender(String sender, String message) {
        return article(
                b(sender + " says:"),
                span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
                p(message)
        ).render();
    }
}
