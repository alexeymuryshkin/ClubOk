package dc.clubok.db.controllers;

import dc.clubok.db.models.Comment;
import dc.clubok.db.models.Post;
import dc.clubok.db.models.User;
import dc.clubok.utils.exceptions.ClubOkException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dc.clubok.utils.Constants.POST_NOT_FOUND;
import static dc.clubok.utils.Constants.model;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

public class PostController {
    private static Logger logger = LoggerFactory.getLogger(PostController.class.getCanonicalName());


    public static List<Post> getPosts(int size, int page, String orderBy, String order, Bson include, Bson exclude) throws ClubOkException {
        return model.findMany(size, page, orderBy, order, include, exclude, Post.class);
    }

    public static void createPost(Post post, User user) throws ClubOkException {
        post.setUserId(user.getId());
        model.saveOne(post, Post.class);
//        ClubOKService.broadcastPost(post);
    }

    public static void commentPost(String postId, Comment comment) throws ClubOkException {
        Post post = PostController.getPostById(postId);
        if (post == null) {
            throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
        }
        model.addOneToArray(post, "comments", comment, Post.class);
    }

    public static void likePost(String postId, String token) throws ClubOkException {
        User user = UserController.getUserByToken(token);
        Post post = PostController.getPostById(postId);
        if (post == null) {
            throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
        }
        model.addOneToSet(post, "likes", user.getId(), Post.class);
    }

    public static Post getPostById(String postId) throws ClubOkException {
        return model.findById(postId, Post.class);
    }

    public static Comment getCommentByCommentId(Post post, String commentId) throws ClubOkException {
        return post.getComments()
                .stream().filter(comment -> comment.getId().toHexString().equals(commentId))
                .collect(Collectors.toList()).get(0);
    }

    public static List<Comment> getCommentsByPostId(String postId) throws ClubOkException {
        Post post = getPostById(postId);
        if (post == null) {
            throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
        }
        return post.getComments();
    }

    public static Set<ObjectId> getLikesByPostId(String postId) throws ClubOkException {
        Post post = getPostById(postId);
        if (post == null) {
            throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
        }
        return post.getLikes();
    }

    public static void deletePostById(String postId) throws ClubOkException {
        Post post = getPostById(postId);
        if (post == null) {
            throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
        }
        model.deleteById(postId, Post.class);
    }

    public static void editPost(String postId, Document update) throws ClubOkException {
        Post post = getPostById(postId);
        if (post == null) {
            throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
        }
        model.modify(post, update, Post.class);
    }

    public static void editComment(String postId, String commentId, Document update) throws ClubOkException {
        Post post = PostController.getPostById(postId);
        if (post == null) {
            throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
        }
        Comment comment = getCommentByCommentId(post, commentId);

        model.modifyOneFromArray(post, "comments", comment, update, Post.class);

    }

    public static void deleteComment(String postId, String commentId) throws ClubOkException {
        Post post = getPostById(postId);
        if (post == null) {
            throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
        }
        Comment comment = getCommentByCommentId(post, commentId);

        model.removeOneFromArray(post, "comments", comment, Post.class);
    }

    public static void deleteLike(String postId, String token) throws ClubOkException {
        Post post = getPostById(postId);
        if (post == null) {
            throw new ClubOkException(POST_NOT_FOUND, "Post does not exist", SC_NOT_FOUND);
        }
        User user = UserController.getUserByToken(token);

        model.removeOneFromArray(post, "likes", user.getId(), Post.class);
    }
}
