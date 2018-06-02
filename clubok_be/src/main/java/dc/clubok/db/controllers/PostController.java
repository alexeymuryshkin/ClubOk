package dc.clubok.db.controllers;

import dc.clubok.db.models.*;
import dc.clubok.utils.ClubOkException;
import dc.clubok.utils.SearchParams;
import org.bson.Document;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dc.clubok.utils.Constants.ERROR_QUERY;
import static dc.clubok.utils.Constants.model;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

public class PostController {
    public static List<Post> getPosts(SearchParams params) throws ClubOkException {
        return model.findByParams(params, Post.class);
    }

    public static void createPost(Post post, Club club, User user) throws ClubOkException {
        if (user != null) post.setUser(new PostUserInfo(user));
        if (club != null) post.setClub(new PostClubInfo(club));
        post.setPostedAt(post.getId().getTimestamp());
        model.saveOne(post, Post.class);
    }

    public static void createManyPosts(List<Post> posts) throws ClubOkException {
        model.saveMany(posts, Post.class);
    }

    public static void commentPost(String postId, Comment comment) throws ClubOkException {
        Post post = PostController.getPostById(postId);
        if (post == null) {
            Document details = new Document("details", "Such post does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }
        model.addOneToArray(post, "comments", comment, Post.class);
    }

    public static void likePost(String postId, User user) throws ClubOkException {
        Post post = PostController.getPostById(postId);
        if (post == null) {
            Document details = new Document("details", "Such post does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }
        model.addOneToSet(post, "likes", user.getId().toHexString(), Post.class);
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
            Document details = new Document("details", "Such post does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }
        return post.getComments();
    }

    public static Set<String> getLikesByPostId(String postId) throws ClubOkException {
        Post post = getPostById(postId);
        if (post == null) {
            Document details = new Document("details", "Such post does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }
        return post.getLikes();
    }

    public static void deletePostById(String postId) throws ClubOkException {
        Post post = getPostById(postId);
        if (post == null) {
            Document details = new Document("details", "Such post does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }
        model.deleteById(postId, Post.class);
    }

    public static void editPost(String postId, Document update) throws ClubOkException {
        Post post = getPostById(postId);
        if (post == null) {
            Document details = new Document("details", "Such post does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }
        model.modify(post, update, Post.class);
    }

    public static void editComment(String postId, String commentId, Document update) throws ClubOkException {
        Post post = PostController.getPostById(postId);
        if (post == null) {
            Document details = new Document("details", "Such post does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }
        Comment comment = getCommentByCommentId(post, commentId);

        model.modifyOneFromArray(post, "comments", comment, update, Post.class);

    }

    public static void deleteComment(String postId, String commentId) throws ClubOkException {
        Post post = getPostById(postId);
        if (post == null) {
            Document details = new Document("details", "Such post does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }
        Comment comment = getCommentByCommentId(post, commentId);

        model.removeOneFromArray(post, "comments", comment, Post.class);
    }

    public static void deleteLike(String postId, String token) throws ClubOkException {
        Post post = getPostById(postId);
        if (post == null) {
            Document details = new Document("details", "Such post does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }
        User user = UserController.getUserByToken(token);

        model.removeOneFromArray(post, "likes", user.getId().toHexString(), Post.class);
    }
}
