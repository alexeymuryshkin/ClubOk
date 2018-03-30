package dc.clubok.controllers;

import dc.clubok.ClubOKService;
import dc.clubok.models.Comment;
import dc.clubok.models.Post;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.*;

import java.util.ArrayList;
import java.util.List;

import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;

public class PostController {
    private static Logger logger = LoggerFactory.getLogger(ClubController.class.getCanonicalName());

    public static Route createPost = (Request request, Response response) -> {
        logger.debug("POST /posts");
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
        logger.debug("POST /posts/" + request.params(":id") + "/comments");
        try {
            Post post = model.findById(new ObjectId(request.params(":id")), Post.class);
            Comment object = gson.fromJson(request.body(), Comment.class);
            if (post == null) {
                response.type(JSON);
                response.status(SC_NOT_FOUND);
                return "";

            }
            post.getComments().add(object);
            model.update(post, new Document("_id", new ObjectId(request.params(":id"))), Post.class);
            response.status(SC_OK);
            return model.findById(new ObjectId(request.params(":id")), Post.class).getComments();
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route addLikeToPost = (Request request, Response response) -> {
        logger.debug("POST /posts/" + request.params(":id") + "/likes");
        try {
            Post post = model.findById(new ObjectId(request.params(":id")), Post.class);
            String object = UserController.findByToken(request.headers("x-auth")).getId().toHexString();
            if (post == null) {
                response.type(JSON);
                response.status(SC_NOT_FOUND);
                return "";

            }else if (post.getLikes().contains(object)){
                response.type(JSON);
                response.status(SC_CONFLICT);
                return "";
            }
            post.getLikes().add(object);
            model.update(post, new Document("_id", new ObjectId(request.params(":id"))), Post.class);
            response.status(SC_OK);
            return model.findById(new ObjectId(request.params(":id")), Post.class).getLikes();
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route fetchAllPosts = (Request request, Response response) -> {
        logger.debug("GET /posts");

        try {
            response.type(JSON);
            response.status(SC_OK);
            return model.findAll(Post.class);
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route getPostById = (Request request, Response response) -> {

        logger.debug("GET /posts/" + request.params(":id"));

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
        logger.debug("GET /posts/" + request.params(":id") + "/comments");

        try {
            Post post = model.findById(new ObjectId(request.params(":id")), Post.class);
            if (post != null) {
                response.type(JSON);
                response.status(SC_OK);
                return post.getComments();
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

    public static Route getLikesByPostId = (Request request, Response response) -> {
        logger.debug("GET /posts/" + request.params(":id") + "/likes");

        try {
            Post post = model.findById(new ObjectId(request.params(":id")), Post.class);
            if (post != null) {
                response.type(JSON);
                response.status(SC_OK);
                return post.getLikes();
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

    public static Route deletePostById = (Request request, Response response) -> {
        logger.debug("DELETE /posts/" + request.params(":id"));
        try {
            model.deleteOne(new Document("_id", new ObjectId(request.params(":id"))), Post.class);
            response.type(JSON);
            response.status(SC_OK);
            return "";
        } catch (IllegalArgumentException e) {
            response.type(JSON);
            response.status(SC_NOT_FOUND);
            return "";
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route editPostById = (Request request, Response response) -> {
        logger.debug("PATCH /posts/" + request.params(":id"));
        try {
            Post post = gson.fromJson(request.body(), Post.class);
            if (post == null) {
                response.type(JSON);
                response.status(SC_NOT_FOUND);
                return "";
            }
            model.update(post, new Document("_id", new ObjectId(request.params(":id"))), Post.class);
            response.type(JSON);
            response.status(SC_OK);
            return post;
        } catch (IllegalArgumentException e) {
            response.type(JSON);
            response.status(SC_NOT_FOUND);
            return "";
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route editCommentByPostId = (Request request, Response response) -> {
        logger.debug("PATCH /posts/" + request.params(":id") + "/comments/" + request.params(":cid"));
        try{
            Post post = model.findById(new ObjectId(request.params(":id")), Post.class);
            int c = 0;
            if (post != null) {
                for (Comment i : post.getComments()) {
                    if (i.getId().toHexString().equals(new ObjectId(request.params(":cid")).toHexString())) {
                        Comment comment = gson.fromJson(request.body(), Comment.class);
                        post.getComments().set(c, comment);
                        model.update(post, new Document("_id", new ObjectId(request.params(":id"))), Post.class);
                        response.type(JSON);
                        response.status(SC_OK);
                        return post;
                    }
                    c++;
                }
            }
            response.type(JSON);
            response.status(SC_NOT_FOUND);
            return "";
        } catch (IllegalArgumentException e) {
            response.type(JSON);
            response.status(SC_NOT_FOUND);
            return "";
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route deleteCommentByPostId = (Request request, Response response) -> {
        logger.debug("DELETE /posts/" + request.params(":id") + "/comments/" + request.params(":cid"));
        try{
            Post post = model.findById(new ObjectId(request.params(":id")), Post.class);
            int c = 0;
            if (post != null) {
                for (Comment i : post.getComments()) {
                    if (i.getId().toHexString().equals(new ObjectId(request.params(":cid")).toHexString())) {
                        post.getComments().remove(c);
                        model.update(post, new Document("_id", new ObjectId(request.params(":id"))), Post.class);
                        response.type(JSON);
                        response.status(SC_OK);
                        return post;
                    }
                    c++;
                }
            }
            response.type(JSON);
            response.status(SC_NOT_FOUND);
            return "";
        } catch (IllegalArgumentException e) {
            response.type(JSON);
            response.status(SC_NOT_FOUND);
            return "";
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route deleteLikesByPostId = (Request request, Response response) -> {
        logger.debug("DELETE /posts/" + request.params(":id") + "/likes");
        try {
            Post post = model.findById(new ObjectId(request.params(":id")), Post.class);
            String UserId = UserController.findByToken(request.headers("x-auth")).getId().toHexString();
            if (post == null || !post.getLikes().contains(UserId)){
                response.type(JSON);
                response.status(SC_NOT_FOUND);
                return "";
            }
            post.getLikes().remove(UserId);
            model.update(post, new Document("_id", new ObjectId(request.params(":id"))), Post.class);
            response.type(JSON);
            response.status(SC_OK);
            return post.getLikes();
        } catch (IllegalArgumentException e) {
            response.type(JSON);
            response.status(SC_NOT_FOUND);
            return "";
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };
}
