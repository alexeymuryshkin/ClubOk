//package dc.clubok.controllers;
//
//import dc.clubok.ClubOKService;
//import dc.clubok.models.Comment;
//import dc.clubok.models.Post;
//import org.bson.Document;
//import org.bson.types.ObjectId;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import spark.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static dc.clubok.utils.Constants.*;
//import static org.apache.http.HttpStatus.*;
//
//public class PostController {
//
//
//    public static Route deleteCommentByPostId = (Request request, Response response) -> {
//    };
//
//    public static Route deleteLikesByPostId = (Request request, Response response) -> {
//        logger.debug("DELETE /posts/" + request.params(":id") + "/likes");
//        try {
//            Post post = model.findById(new ObjectId(request.params(":id")), Post.class);
//            String UserId = UserController.findByToken(request.headers("x-auth")).getId().toHexString();
//            if (post == null || !post.getLikes().contains(UserId)){
//                response.type(JSON);
//                response.status(SC_NOT_FOUND);
//                return "";
//            }
//            post.getLikes().remove(UserId);
//            model.update(post, new Document("_id", new ObjectId(request.params(":id"))), Post.class);
//            response.type(JSON);
//            response.status(SC_OK);
//            return post.getLikes();
//        } catch (IllegalArgumentException e) {
//            response.type(JSON);
//            response.status(SC_NOT_FOUND);
//            return "";
//        } catch (Exception e) {
//            response.type(JSON);
//            response.status(SC_BAD_REQUEST);
//            return e;
//        }
//    };
//}
