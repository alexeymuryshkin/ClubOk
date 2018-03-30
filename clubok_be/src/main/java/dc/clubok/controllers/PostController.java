package dc.clubok.controllers;

import dc.clubok.ClubOKService;
import dc.clubok.models.Post;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.*;

import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;

public class PostController {
    private static Logger logger = LoggerFactory.getLogger(ClubController.class.getCanonicalName());

    public static Route createPost = (Request request, Response response) -> {
        logger.debug("POST /posts " + request.body());
        try {
            Post post = gson.fromJson(request.body(), Post.class);
            post.setUserId(UserController.findByToken(request.headers("x-auth")).getId().toHexString());
            model.saveOne(post, Post.class);

            response.type(JSON);
            response.status(SC_OK);
            ClubOKService.broadcastPost(post);
            return post;
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route addCommentToPost = (Request request, Response response) -> {
//        TODO
        return "";
    };

    public static Route addLikeToPost = (Request request, Response response) -> {
//        TODO
        return "";
    };

    public static Route fetchAllPosts = (Request request, Response response) -> {
//  TODO
        return "";
    };

    public static Route getPostById = (Request request, Response response) -> {
        try {
            Post post = model.findById(new ObjectId(request.params(":id")), Post.class);
            if (post != null) {
                response.type(JSON);
                response.status(SC_OK);
                return post;
            } else {
                response.status(SC_NOT_FOUND);
                return "";
            }
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route getCommentsByPostId = (Request request, Response response) -> {
//        TODO
        return "";
    };

    public static Route getLikesByPostId = (Request request, Response response) -> {
//        TODO
        return "";
    };

    public static Route deletePostById = (Request request, Response response) -> {
//        TODO
        return "";
    };

    public static Route editPostById = (Request request, Response response) -> {
//        TODO
        return "";
    };
}
