package dc.clubok.routes;

import dc.clubok.db.controllers.PostController;
import dc.clubok.db.models.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import static dc.clubok.utils.Constants.*;

public class PostRoute {
    private static Logger logger = LoggerFactory.getLogger(PostRoute.class.getCanonicalName());

    public static Route GetPosts = (Request request, Response response) -> {
        logger.debug("GET /posts " + request.queryString());
        try {
            int page = Integer.parseInt(request.queryParamOrDefault("page", "1"));
            logger.debug(String.valueOf(page));

            return ok(response, PostController.getPosts(request.queryString()));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return badRequest(response, e);
        }
    };

    public static Route PostPosts = (Request request, Response response) -> {
        logger.debug("POST /posts " + request.body());

        try {
            PostController.createPost(gson.fromJson(request.body(), Post.class));
            return created(response);
        } catch (Exception e) {
            return badRequest(response, e);
        }
    };

    public static Route GetPostsId = (Request request, Response response) -> {
        logger.debug("GET /posts/" + request.params(":id"));

        try {
            Post post = PostController.getPostById(request.params(":id"));
            if (post == null) {
                return notFound(response);
            }

            return ok(response, post);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return badRequest(response, e);
        }
    };

    public static Route GetPostsIdComments = (Request request, Response response) -> {
        logger.debug("GET /posts/" + request.params(":id") + "/comments");
//        TODO
        return notFound(response);
    };

    public static Route GetPostsIdLikes = (Request request, Response response) -> {
        logger.debug("GET /posts/" + request.params(":id") + "/likes");
//        TODO
        return notFound(response);
    };

    public static Route PostPostsIdComments = (Request request, Response response) -> {
        logger.debug("GET /users/" + request.params(":id") + "/comments");
//        TODO
        return notFound(response);
    };

    public static Route PostPostsIdLikes = (Request request, Response response) -> {
        logger.debug("GET /users/" + request.params(":id") + "/likes");
//        TODO
        return notFound(response);
    };

    public static Route DeletePostsId = (Request request, Response response) -> {
        logger.debug("DELETE /posts/" + request.params(":id"));
//        TODO
        return notFound(response);
    };

    public static Route PatchPostsId = (Request request, Response response) -> {
        logger.debug("PATCH /posts/" + request.params(":id") + " " + request.body());
//        TODO
        return notFound(response);
    };
}
