//package dc.clubok.db.controllers;
//
//import dc.clubok.models.Club;
//import dc.clubok.models.User;
//import org.apache.http.client.methods.HttpUriRequest;
//import org.apache.http.client.methods.RequestBuilder;
//import org.apache.http.entity.StringEntity;
//import org.bson.Document;
//import org.bson.types.ObjectId;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import spark.Request;
//import spark.Response;
//import spark.Route;
//
//import javax.jws.soap.SOAPBinding;
//
//import static dc.clubok.utils.Constants.JSON;
//import static dc.clubok.utils.Constants.gson;
//import static dc.clubok.utils.Constants.model;
//import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
//import static org.apache.http.HttpStatus.SC_CONFLICT;
//import static org.apache.http.HttpStatus.SC_NOT_FOUND;
//import static org.apache.http.HttpStatus.SC_OK;
//
//public class SubscriptionController {
//    private static Logger logger = LoggerFactory.getLogger(SubscriptionController.class.getCanonicalName());
//
//    public static Route addSubscription = (Request request, Response response) -> {
//        logger.debug("POST /subscriptions");
//        try {
//            String UserID = UserController.findByToken(request.headers("x-auth")).getId().toHexString();
//            String ClubId = gson.fromJson(request.body(), String.class);
//            User user = model.findById(new ObjectId(UserID), User.class);
//            Club club = model.findById(new ObjectId(ClubId), Club.class);
//
//            if (user == null || club == null){
//                response.type(JSON);
//                response.status(SC_NOT_FOUND);
//                return "";
//            }
//            if (user.getSubscriptions().contains(ClubId) || club.getSubscribers().contains(UserID)){
//                response.type(JSON);
//                response.status(SC_CONFLICT);
//                return "";
//            }
//            user.getSubscriptions().add(ClubId);
//            club.getSubscribers().add(UserID);
//
//            model.update(user, new Document("_id", user.getId()), User.class);
//            model.update(club, new Document("_id", club.getId()), Club.class);
//            response.type(JSON);
//            response.status(SC_OK);
//            return "";
//        } catch (Exception e) {
//            response.type(JSON);
//            response.status(SC_BAD_REQUEST);
//            return e;
//        }
//    };
//
//    public static Route deleteSubscription = (Request request, Response response) -> {
//        logger.debug("DELETE /subscriptions");
//        try {
//            String ClubId = gson.fromJson(request.body(), String.class);
//            String UserId = UserController.findByToken(request.headers("x-auth")).getId().toHexString();
//            User user = model.findById(new ObjectId(UserId), User.class);
//            Club club = model.findById(new ObjectId(ClubId), Club.class);
//            if (user == null || club == null){
//                response.type(JSON);
//                response.status(SC_NOT_FOUND);
//                return "";
//            }
//            if (!user.getSubscriptions().contains(ClubId) && !club.getSubscribers().contains(UserId)){
//                response.type(JSON);
//                response.status(SC_NOT_FOUND);
//                return "";
//            }
//            for (int i = 0; i < user.getSubscriptions().size(); i++){
//                if (user.getSubscriptions().get(i).equals(ClubId)){
//                    user.getSubscriptions().remove(i);
//                    break;
//                }
//            }
//            for (int i = 0; i < club.getSubscribers().size(); i++){
//                if (club.getSubscribers().get(i).equals(UserId)){
//                    club.getSubscribers().remove(i);
//                    break;
//                }
//            }
//            model.update(user, new Document("_id", user.getId()), User.class);
//            model.update(club, new Document("_id", club.getId()), Club.class);
//            response.type(JSON);
//            response.status(SC_OK);
//            return "";
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
