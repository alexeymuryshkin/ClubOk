package dc.clubok.db.controllers;

import dc.clubok.ClubOKService;
import dc.clubok.db.models.Comment;
import dc.clubok.db.models.Post;
import dc.clubok.db.models.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static dc.clubok.utils.Constants.model;

public class PostController {
    private static Logger logger = LoggerFactory.getLogger(ClubController.class.getCanonicalName());

    public static void createPost(Post post) throws Exception {
        model.saveOne(post, Post.class);
        ClubOKService.broadcastPost(post);
    }

    public static void commentPost(Post post, Comment comment) throws Exception {
        post.getComments().add(comment);
        model.update(post, combine(set("comments", post.getComments())), Post.class);
    }

    public static void likePost(Post post, User user) throws Exception {
        post.getLikes().add(user.getId().toHexString());
        model.update(post, new Document("likes", post.getLikes()), Post.class);
    }

    public static List<Post> getPosts(String params) throws Exception {
        return null;
    }

    public static Post getPostById(String id) throws Exception {
        return model.findById(id, Post.class);
    }

    public static List<Comment> getCommentsByPostId(String id) throws Exception {
        Post post = model.findById(id, Post.class);
        return post.getComments();
    }

    public static Set<String> getLikesByPostId(String id) throws Exception {
        Post post = model.findById(id, Post.class);
        return post.getLikes();
    }

    public static void deletePostById(String id) throws Exception {
        model.deleteById(id, Post.class);
    }

    public static void editPost(String id, Document update) throws Exception {
        model.update(getPostById(id), update, Post.class);
    }

    public static void editComment(Post post, String id, Comment update) throws Exception {
        Document query = new Document("_id", post.getId())
                .append("comments._id", new ObjectId(id));

        model.update(query, new Document("$set", new Document(
                "comments.$", update
        )), Post.class);

    }

//    public static void deleteComment(Post post, String id) throws Exception {
//        List<Comment> comment = post.getComments().stream().filter(comment1 -> comment1.getId().toHexString().equals(id)).collect(Collectors.toList());
//        logger.debug(post.getComments().lastIndexOf());
//        logger.debug(String.valueOf(comment.size()));
//
//
//        model.update(post, pull("comments", comment.get(0)), Post.class);
//    }
}
