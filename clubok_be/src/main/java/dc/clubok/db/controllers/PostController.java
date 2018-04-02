package dc.clubok.db.controllers;

import dc.clubok.ClubOKService;
import dc.clubok.db.models.Comment;
import dc.clubok.db.models.Post;
import dc.clubok.db.models.User;
import dc.clubok.utils.exceptions.ClubOkException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Updates.set;
import static dc.clubok.utils.Constants.POST_NOT_FOUND;
import static dc.clubok.utils.Constants.model;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

public class PostController {
    private static Logger logger = LoggerFactory.getLogger(ClubController.class.getCanonicalName());

    public static void createPost(Post post, String userId) throws ClubOkException {
        post.setUserId(userId);
        model.saveOne(post, Post.class);
        ClubOKService.broadcastPost(post);
    }

    public static void commentPost(String postId, Comment comment) throws ClubOkException {
        Post post = PostController.getPostById(postId);
        if (post == null) {
            throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
        }
        post.getComments().add(comment);
//        TODO Change update to only push comment without replacing whole comments entry
        model.update(post, set("comments", post.getComments()), Post.class);
    }

    public static void likePost(String postId, String token) throws ClubOkException {
        User user = UserController.getUserByToken(token);
        Post post = PostController.getPostById(postId);
        if (post == null) {
            throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
        }

        post.getLikes().add(user.getId().toHexString());
//        TODO Change update to only push likes without repalacing whole array
        model.update(post, set("likes", post.getLikes()), Post.class);
    }

    public static List<Post> getPosts(String params) throws ClubOkException {
//        TODO
        return null;
    }

    public static Post getPostById(String postId) throws ClubOkException {
        return model.findById(postId, Post.class);
    }

    public static List<Comment> getCommentsByPostId(String postId) throws ClubOkException {
        Post post = model.findById(postId, Post.class);
        if (post == null) {
            throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
        }
        return post.getComments();
    }

    public static Set<String> getLikesByPostId(String postId) throws ClubOkException {
        Post post = model.findById(postId, Post.class);
        if (post == null) {
            throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
        }
        return post.getLikes();
    }

    public static void deletePostById(String postId) throws ClubOkException {
        Post post = model.findById(postId, Post.class);
        if (post == null) {
            throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
        }
        model.deleteById(postId, Post.class);
    }

    public static void editPost(String postId, Document update) throws ClubOkException {
        Post post = model.findById(postId, Post.class);
        if (post == null) {
            throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
        }
        model.update(getPostById(postId), update, Post.class);
    }

    public static void editComment(String postId, String commentId, Comment update) throws ClubOkException {
        Post post = PostController.getPostById(postId);
        if (post == null) {
            throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
        }
        Document query = new Document("_id", post.getId())
                .append("comments._id", new ObjectId(commentId));

        model.update(query, new Document("$set", new Document(
                "comments.$", update
        )), Post.class);

    }

    public static void deleteComment(String postId, String commentId) throws ClubOkException {
//        List<Comment> comment = post.getComments().stream().filter(comment1 -> comment1.getId().toHexString().equals(id)).collect(Collectors.toList());
//        logger.debug(post.getComments().lastIndexOf());
//        logger.debug(String.valueOf(comment.size()));
//
//
//        model.update(post, pull("comments", comment.get(0)), Post.class);
    }

    public static void deleteLike(String postId, String token) throws ClubOkException {

    }
}
