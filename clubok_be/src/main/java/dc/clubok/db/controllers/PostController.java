package dc.clubok.db.controllers;

import dc.clubok.ClubOKService;
import dc.clubok.db.models.Comment;
import dc.clubok.db.models.Post;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static dc.clubok.utils.Constants.model;

public class PostController {
    private static Logger logger = LoggerFactory.getLogger(ClubController.class.getCanonicalName());

    public static void createPost(Post post) throws Exception {
        model.saveOne(post, Post.class);
        ClubOKService.broadcastPost(post);
    }

    public static void commentPost(Post post, Comment comment) throws Exception {
//        TODO
    }

    public static void likePost(Post post) throws Exception {
//        TODO
    }

    public static List<Post> getPosts(String params) throws Exception {
        return null;
    }

    public static Post getPostById(String id) throws Exception {
        return model.findById(id, Post.class);
    }

    public static List<Comment> getCommentsByPostId(String id) throws Exception {
//        TODO
        return null;
    }

    public static List<ObjectId> getLikesByPostId(String id) throws Exception {
//        TODO
        return null;
    }

    public static void deletePostById(String id) throws Exception {
//        TODO
    }

    public static void editPostById(String id) throws Exception {
//        TODO
    }
}
