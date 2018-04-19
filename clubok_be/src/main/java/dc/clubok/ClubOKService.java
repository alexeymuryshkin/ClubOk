package dc.clubok;

import dc.clubok.db.models.Post;
import dc.clubok.utils.Constants;
import dc.clubok.utils.exceptions.ClubOkException;
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
            before("/*", (request, response) -> {
                String log_template = "Received API call: %s %s\nHeaders:\n%sBody:\n%s";
                StringBuilder headers = new StringBuilder();
                for (String header : request.headers()) {
                    headers.append("\t").append(header).append(": ").append(request.headers(header)).append("\n");
                }
                String body = request.body();

                logger.info(String.format(log_template,
                        request.requestMethod(),
                        request.uri(),
                        headers.toString(),
                        body
                ));
            });
            for (String r: Constants.protectedRoutes) {
                before(r, (request, response) -> {
                    if (notAuthenticated(request, response)) {
                        throw halt(401);
                    }
                });
            }

            path("/users", () -> {
                get("", GetUsers, gson::toJson);
                post("", JSON, PostUsers, gson::toJson);
                post("/login", JSON, PostUsersLogin, gson::toJson);

                path("/me", () -> {
                    get("", GetUsersMe, gson::toJson);

                    get("/subscriptions", GetUsersMeSubscriptions, gson::toJson);
                    delete("/subscriptions/:id", DeleteUsersMeSubscriptionsId, gson::toJson);

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
                get("", GetClubs, gson::toJson);
                post("", JSON, PostClubs, gson::toJson);

                path("/:id", () -> {
                    get("", GetClubsId, gson::toJson);
                    delete("", DeleteClubsId, gson::toJson);
                    patch("", JSON, PatchClubsId, gson::toJson);

                    get("/subscribers", GetClubsIdSubscribers, gson::toJson);
                    delete("/subscribers/:uid", DeleteClubsIdSubscribersId, gson::toJson);

                    get("/participants", GetClubsIdParticipants, gson::toJson);
                    delete("/participants/:uid", DeleteClubsIdParticipantsId, gson::toJson);

                    get("/moderators", GetClubsIdModerators, gson::toJson);
                    delete("/moderators/:uid", DeleteClubsIdModeratorsId, gson::toJson);
                });
            });

            path("/posts", () -> {
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
