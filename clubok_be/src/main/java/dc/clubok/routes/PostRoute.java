package dc.clubok.routes;

import dc.clubok.db.controllers.PostController;
import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.Comment;
import dc.clubok.db.models.Post;
import dc.clubok.db.models.User;
import dc.clubok.utils.ClubOkException;
import dc.clubok.utils.SearchParams;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;

public class PostRoute {
    private static Logger logger = LoggerFactory.getLogger(PostRoute.class.getCanonicalName());

    public static Route GetPosts = (Request request, Response response) -> {
        try {
            SearchParams params = new SearchParams(request.queryMap().toMap());
            List<Post> posts = PostController.getPosts(params);

            Document result = new Document("total", posts.size())
                    .append("results", posts);

            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route PostPosts = (Request request, Response response) -> {
        try {
            Post post = gson.fromJson(request.body(), Post.class);
            User user = UserController.getUserByToken(request.headers("x-auth"));
            PostController.createPost(post, user);

            Document result = new Document("post_id", post.getId().toHexString());

            return response(response, SC_CREATED, SUCCESS_CREATE, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route GetPostsId = (Request request, Response response) -> {
        try {
                Post post = PostController.getPostById(request.params(":id"));
            if (post == null) {
                Document details = new Document("details", "Such post does not exist");
                throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
            }

            Document result = new Document("result", post);

            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route DeletePostsId = (Request request, Response response) -> {
        try {
            PostController.deletePostById(request.params(":id"));
            return response(response, SC_NO_CONTENT, SUCCESS_DELETE);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route PatchPostsId = (Request request, Response response) -> {
        try {
            Document update = Document.parse(request.body());
            PostController.editPost(request.params(":id"), update);
            return response(response, SC_OK, SUCCESS_EDIT);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route GetPostsIdComments = (Request request, Response response) -> {
        try {
            Document result = new Document("results", PostController.getCommentsByPostId(request.params(":id")));
            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route PostPostsIdComments = (Request request, Response response) -> {
        try {
            Comment comment = gson.fromJson(request.body(), Comment.class);
            comment.setUserId(UserController.getUserByToken(request.headers("x-auth")).getId().toHexString());
            PostController.commentPost(request.params(":id"), comment);

            Document result = new Document("comment_id", comment.getId().toHexString());

            return response(response, SC_CREATED, SUCCESS_CREATE, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route DeletePostsIdCommentId = (Request request, Response response) -> {
        try{
            PostController.deleteComment(request.params(":id"), request.params(":cid"));

            return response(response, SC_NO_CONTENT, SUCCESS_DELETE);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route PatchPostsIdCommentsId = (Request request, Response response) -> {
        try{
            Document update = Document.parse(request.body());
            PostController.editComment(request.params(":id"), request.params(":cid"), update);

            return response(response, SC_NO_CONTENT, SUCCESS_EDIT);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route GetPostsIdLikes = (Request request, Response response) -> {
        try {
            Document result = new Document("results", PostController.getLikesByPostId(request.params(":id")));
            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route PostPostsIdLikes = (Request request, Response response) -> {
        try {
            PostController.likePost(request.params(":id"), request.headers("x-auth"));
            return response(response, SC_CREATED, SUCCESS_CREATE);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route DeletePostsIdLikes = (Request request, Response response) -> {
        try{
            PostController.deleteLike(request.params(":id"), request.headers("x-auth"));

            return response(response, SC_NO_CONTENT, SUCCESS_DELETE);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };
}
