package dc.clubok.routes;

import dc.clubok.db.controllers.PostController;
import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.Comment;
import dc.clubok.db.models.Post;
import dc.clubok.utils.exceptions.ClubOkException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.include;
import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;

public class PostRoute {
    private static Logger logger = LoggerFactory.getLogger(PostRoute.class.getCanonicalName());

    public static Route GetPosts = (Request request, Response response) -> {
        try {
            int size = request.queryParams("size") == null ? 50 : Integer.parseInt(request.queryParams("size"));
            int page = request.queryParams("page") == null ? 1 : Integer.parseInt(request.queryParams("page"));
            String orderBy = request.queryParams("orderBy") == null ? "postedAt" : request.queryParams("orderBy");
            String order = request.queryParams("order") == null ? "descending" : request.queryParams("order");

            Bson include = include();
            Bson exclude = exclude();

            Document document = new Document("total", model.count(Post.class))
                    .append("results", PostController.getPosts(size, page, orderBy, order, include, exclude));

            return response(response, SC_OK, document);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route PostPosts = (Request request, Response response) -> {
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
        try{
            Document update = Document.parse(request.body());
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
        try{
            PostController.deleteLike(request.params(":id"), request.headers("x-auth"));

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
