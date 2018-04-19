package dc.clubok.routes;

import dc.clubok.db.controllers.PostController;
import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.Comment;
import dc.clubok.db.models.Post;
import dc.clubok.utils.exceptions.ClubOkException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;

public class PostRoute {
    private static Logger logger = LoggerFactory.getLogger(PostRoute.class.getCanonicalName());

    public static Route GetPosts = (Request request, Response response) -> {
        logger.debug("GET /posts " + request.queryString());
        try {
            int page = Integer.parseInt(request.queryParamOrDefault("page", "1"));
            logger.debug(String.valueOf(page));

            return response(response, SC_OK, PostController.getPosts(request.queryString()));
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route PostPosts = (Request request, Response response) -> {
        logger.debug("POST /posts " + request.body());

        try {
            Post post = gson.fromJson(request.body(), Post.class);
            PostController.createPost(post, UserController.getUserByToken(request.headers("x-auth")).getId().toHexString());

            return response(response, SC_CREATED, post.getId().toHexString());
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route GetPostsId = (Request request, Response response) -> {
        logger.debug("GET /posts/" + request.params(":id"));

        try {
            Post post = PostController.getPostById(request.params(":id"));
            if (post == null) {
                throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
            }

            return response(response, SC_OK, post);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route DeletePostsId = (Request request, Response response) -> {
        logger.debug("DELETE /posts/" + request.params(":id"));

        try {
            PostController.deletePostById(request.params(":id"));
            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route PatchPostsId = (Request request, Response response) -> {
        logger.debug("PATCH /posts/" + request.params(":id") + " " + request.body());

        try {
            Document update = Document.parse(request.body());
            PostController.editPost(request.params(":id"), update);
            return response(response, SC_OK);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route GetPostsIdComments = (Request request, Response response) -> {
        logger.debug("GET /posts/" + request.params(":id") + "/comments");

        try {
            return response(response, SC_CREATED, PostController.getCommentsByPostId(request.params(":id")));
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route PostPostsIdComments = (Request request, Response response) -> {
        logger.debug("POST /posts/" + request.params(":id") + "/comments" + request.body());

        try {
            Comment comment = gson.fromJson(request.body(), Comment.class);
            comment.setUserId(UserController.getUserByToken(request.headers("x-auth")).getId().toHexString());
            PostController.commentPost(request.params(":id"), comment);

            return response(response, SC_OK, comment.getId().toHexString());
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route DeletePostsIdCommentId = (Request request, Response response) -> {
        logger.debug("DELETE /posts/" + request.params(":id") + "/comments/" + request.params(":cid"));

        try{
            PostController.deleteComment(request.params(":id"), request.params(":cid"));

            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route PatchPostsIdCommentsId = (Request request, Response response) -> {
        logger.debug("PATCH /posts/" + request.params(":id") + "/comments/" + request.params(":cid") + " " + request.body());

        try{
            Comment update = gson.fromJson(request.body(), Comment.class);
            update.setUserId(UserController.getUserByToken(request.headers("x-auth")).getId().toHexString());

            PostController.editComment(request.params(":id"), request.params(":cid"), update);

            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route GetPostsIdLikes = (Request request, Response response) -> {
        logger.debug("GET /posts/" + request.params(":id") + "/likes");

        try {
            return response(response, SC_OK, PostController.getLikesByPostId(request.params(":id")));
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route PostPostsIdLikes = (Request request, Response response) -> {
        logger.debug("POST /posts/" + request.params(":id") + "/likes");

        try {
            PostController.likePost(request.params(":id"), request.headers("x-auth"));
            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route DeletePostsIdLikes = (Request request, Response response) -> {
        logger.debug("DELETE /posts/" + request.params(":id") + "/likes/" + request.headers("x-auth"));

        try{
            PostController.deleteComment(request.params(":id"), request.headers("x-auth"));

            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };
}
