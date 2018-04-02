package dc.clubok;

import dc.clubok.db.models.Event;
import dc.clubok.db.models.Post;
import dc.clubok.utils.exceptions.ClubOkException;
import org.bson.Document;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static dc.clubok.routes.ClubRoute.*;
import static dc.clubok.routes.EventRoute.*;
import static dc.clubok.routes.PostRoute.*;
import static dc.clubok.routes.SubscriptionRoute.DeleteSubscriptions;
import static dc.clubok.routes.SubscriptionRoute.PostSubscriptions;
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
                    delete("", DeleteUsersId, gson::toJson);

                    get("/subscriptions", GetUsersIdSubscriptions, gson::toJson);

                    get("/tokens", GetUsersIdTokens, gson::toJson);
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
                    delete("", DeleteClubsId, gson::toJson);
                    patch("", JSON, PatchClubsId, gson::toJson);

                    get("/subscribers", GetClubsIdSubscribers, gson::toJson);

                    get("/participants", GetClubsIdParticipants, gson::toJson);

                    get("/moderators", GetClubsIdModerators, gson::toJson);
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
                    delete("", DeletePostsId, gson::toJson);
                    patch("", JSON, PatchPostsId, gson::toJson);

                    get("/comments", GetPostsIdComments, gson::toJson);
                    post("/comments", PostPostsIdComments, gson::toJson);
                    patch("/comments/:cid", JSON, PatchPostsIdCommentsId, gson::toJson);
                    delete("/comments/:cid", DeletePostsIdCommentId, gson::toJson);

                    get("/likes", GetPostsIdLikes, gson::toJson);
                    post("/likes", PostPostsIdLikes, gson::toJson);
                    delete("/likes", DeletePostsIdLikes, gson::toJson);
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

                post("", JSON, PostSubscriptions, gson::toJson);
                delete("", JSON, DeleteSubscriptions, gson::toJson);
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
        } catch (ClubOkException e) {
            e.printStackTrace();
        }
    }
}
