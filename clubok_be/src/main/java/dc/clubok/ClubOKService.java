package dc.clubok;

import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.User;
import dc.clubok.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;

import static dc.clubok.routes.AdministrationRoute.*;
import static dc.clubok.routes.ClubRoute.*;
import static dc.clubok.routes.EventRoute.*;
import static dc.clubok.routes.PostRoute.*;
import static dc.clubok.routes.UserRoute.*;
import static dc.clubok.utils.Constants.*;
import static spark.Spark.*;

public class ClubOKService {
    private static Logger logger = LoggerFactory.getLogger(ClubOKService.class.getCanonicalName());
//    public static Map<Session, String> clients = new ConcurrentHashMap<>();
//    public static int nextUserNumber = 1;

    public static void main(String[] args) {
        port(Integer.valueOf(config.getProperties().getProperty("port")));
        String staticFilesLocation = config.getEnvironment().equals("production") ?
                "/public" :
                System.getProperty("user.dir") + "/clubok_be/src/main/resources/public";

        if (config.getEnvironment().equals("production")) {
            staticFiles.location(staticFilesLocation);
        } else {
            staticFiles.externalLocation(staticFilesLocation);
        }
        notFound((request, response) -> new VelocityTemplateEngine().render(
                new ModelAndView(new HashMap<String, Object>(), "/public/index.html")
        ));
        logger.info("Server started at port " + config.getProperties().getProperty("port"));




//        webSocket("/feed", FeedWebSocketHandler.class);
        path("/api", () -> {
            before("/*", (request, response) -> {
                String log_template = "Received API call: %s %s/%s\nHeaders:\n%s%s";
                StringBuilder headers = new StringBuilder();
                for (String header : request.headers()) {
                    headers.append("\t").append(header).append(": ").append(request.headers(header)).append("\n");
                }
                String body = request.body().isEmpty() ? "" : "Body:\n" + request.body();

                logger.info(String.format(log_template,
                        request.requestMethod(),
                        request.uri(),
                        request.queryString() == null ? "" : request.queryString(),
                        headers.toString(),
                        body
                ));
            });
            for (String r : Constants.protectedRoutes) {
                before(r, Authenticate);
            }

            path("/users", () -> {
                get("", GetUsers, gson::toJson);
                post("", JSON, PostUsers, gson::toJson);
                post("/login", JSON, PostUsersLogin, gson::toJson);

                path("/me", () -> {
                    get("", GetUsersMe, gson::toJson);

                    get("/subscriptions", GetUsersMeSubscriptions, gson::toJson);
                    post("/subscriptions/:id", PostUsersMeSubscriptions, gson::toJson);
                    delete("/subscriptions/:id", DeleteUsersMeSubscriptionsId, gson::toJson);

                    get("/tokens", GetUsersMeTokens, gson::toJson);
                    delete("/tokens", DeleteUsersMeTokens, gson::toJson);
                    delete("/token", DeleteUsersMeToken, gson::toJson);
                });
                path("/:id", () -> {
                    get("", GetUsersId, gson::toJson);
                    get("/subscriptions", GetUsersIdSubscriptions, gson::toJson);
                });
            });

            path("/clubs", () -> {
                get("", GetClubs, gson::toJson);

                path("/:id", () -> {
                    get("", GetClubsId, gson::toJson);
                    patch("", JSON, PatchClubsId, gson::toJson);

                    get("/subscribers", GetClubsIdSubscribers, gson::toJson);
                    post("/subscribers", PostClubsIdSubscribers, gson::toJson);

                    get("/members", GetClubsIdMembers, gson::toJson);
                    post("/members", PostClubsIdMembers, gson::toJson);
                    delete("/members/:uid", DeleteClubsIdMembersId, gson::toJson);
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

            path("/administration", () -> {
                before("/*", (request, response) -> {
                    User user = UserController.getUserByToken(request.headers("x-auth"));
                    if (user.getPermissionLevel() < PL_ADMINISTRATOR) {
                        halt(401);
                    }
                });

                path("/users", () -> {
                    delete("/:id", DeleteAdministrationUsersId, gson::toJson);
                    get("/:id/tokens", GetAdministrationUsersIdTokens, gson::toJson);
//                        delete("/:id/tokens");
//                        delete("/:id/token");

//                        delete("/:id/subscriptions/:cid");
                });

                path("/clubs", () -> {
                    post("", JSON, PostAdministrationClubs, gson::toJson);
                    delete("/:id", DeleteAdministrationClubsId, gson::toJson);
                    delete("/:id/subscribers/:uid", DeleteAdministrationClubsIdSubscribersId, gson::toJson);
                });


            });
        });
        init();
    }

//    public static void broadcastPost(Post post) {
//        clients.keySet().stream().filter(Session::isOpen).forEach(session -> {
//            try {
//                session.getRemote().sendString(gson.toJson(Collections.singletonList(post)));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    public static void sendPost(Post post) {
//
//    }
//
//    public static void sendPosts(Session user) {
//        try {
//            user.getRemote().sendString(gson.toJson(model.findAll(Post.class)));
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        } catch (ClubOkException e) {
//            e.printStackTrace();
//        }
//    }
}
