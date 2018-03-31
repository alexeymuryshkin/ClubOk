package dc.clubok.routes;

import dc.clubok.db.controllers.PostController;
import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.Comment;
import dc.clubok.db.models.Post;
import dc.clubok.db.models.User;
import org.bson.Document;
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
            Post post = gson.fromJson(request.body(), Post.class);
            post.setUserId(UserController.getUserByToken(request.headers("x-auth")).getId().toHexString());

            PostController.createPost(post);
            return created(response, post.getId().toHexString());
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

        try {
            return created(response, PostController.getCommentsByPostId(request.params(":id")));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return badRequest(response, e);
        }
    };

    public static Route GetPostsIdLikes = (Request request, Response response) -> {
        logger.debug("GET /posts/" + request.params(":id") + "/likes");

        try {
            return ok(response, PostController.getLikesByPostId(request.params(":id")));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return badRequest(response, e);
        }
    };

    public static Route PostPostsIdComments = (Request request, Response response) -> {
        logger.debug("POST /posts/" + request.params(":id") + "/comments" + request.body());

        try {
            Post post = PostController.getPostById(request.params(":id"));
            Comment comment = gson.fromJson(request.body(), Comment.class);
            comment.setUserId(UserController.getUserByToken(request.headers("x-auth")).getId().toHexString());

            if (post == null) {
                return notFound(response);
            }
            PostController.commentPost(post, comment);

            return ok(response, comment.getId().toHexString());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return badRequest(response, e);
        }
    };

    public static Route PostPostsIdLikes = (Request request, Response response) -> {
        logger.debug("POST /posts/" + request.params(":id") + "/likes");

        try {
            Post post = PostController.getPostById(request.params(":id"));
            User user = UserController.getUserByToken(request.headers("x-auth"));

            if (post == null) {
                return notFound(response);
            }

            PostController.likePost(post, user);
            return noContent(response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return badRequest(response, e);
        }
    };

    public static Route DeletePostsId = (Request request, Response response) -> {
        logger.debug("DELETE /posts/" + request.params(":id"));

        try {
            PostController.deletePostById(request.params(":id"));
            return noContent(response);
        } catch (IllegalArgumentException e) {
            return notFound(response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return badRequest(response, e);
        }
    };

    public static Route PatchPostsId = (Request request, Response response) -> {
        logger.debug("PATCH /posts/" + request.params(":id") + " " + request.body());

        try {
            Document update = Document.parse(request.body());
            PostController.editPost(request.params(":id"), update);
            return ok(response);
        } catch (IllegalArgumentException e) {
            return notFound(response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return badRequest(response, e);
        }
    };

    public static Route PatchPostsIdCommentsId = (Request request, Response response) -> {
        logger.debug("PATCH /posts/" + request.params(":id") + "/comments/" + request.params(":cid") + " " + request.body());

        try{
            Post post = PostController.getPostById(request.params(":id"));
            Comment update = gson.fromJson(request.body(), Comment.class);
            update.setUserId(UserController.getUserByToken(request.headers("x-auth")).getId().toHexString());

            PostController.editComment(post, request.params(":cid"), update);

            return noContent(response);
        } catch (IllegalArgumentException e) {
            return notFound(response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return badRequest(response, e);
        }
    };

    public static Route DeletePostsIdCommentId = (Request request, Response response) -> {
        logger.debug("DELETE /posts/" + request.params(":id") + "/comments/" + request.params(":cid"));

//        try{
//            Post post = PostController.getPostById(request.params(":id"));
//            PostController.deleteComment(post, request.params(":id"));
//
//            return noContent(response);
//        } catch (IllegalArgumentException e) {
//            return notFound(response);
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            return badRequest(response, e);
//        }
        return notFound(response);
    };

    public static Route DeletePostsIdLikes = (Request request, Response response) -> {
        logger.debug("DELETE /posts/" + request.params(":id") + "/likes/" + request.headers("x-auth"));

//        try{
//            Post post = PostController.getPostById(request.params(":id"));
//            PostController.deleteComment(post, request.params(":id"));
//
//            return noContent(response);
//        } catch (IllegalArgumentException e) {
//            return notFound(response);
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            return badRequest(response, e);
//        }
        return notFound(response);
    };

}
