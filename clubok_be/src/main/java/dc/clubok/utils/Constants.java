package dc.clubok.utils;

import com.google.gson.Gson;
import dc.clubok.config.Config;
import dc.clubok.db.handlers.MongoHandle;
import dc.clubok.db.models.Model;
import dc.clubok.db.mongomodel.MongoModel;
import org.bson.Document;
import spark.Response;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final Config config = new Config();

    /* Content types */
    public static final String JSON = "application/json";

    /* Mongo */
    public static final MongoHandle mongo = new MongoHandle();
    public static final Model model = new MongoModel();

    public static final Gson gson = new Gson();
    public static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /* Protected Routes */
    public static final List<String> protectedRoutes = Arrays.asList(
            "/users/me",
            "/users/me/*",
            "/clubs",
            "/clubs/*",
            "/posts",
            "/posts/*",
            "/events",
            "/events/*",
            "/administration/*"
    );

    /* Permission Levels */
    public static final int PL_GUEST = 0;
    public static final int PL_REGULAR = 1;
    public static final int PL_ADMINISTRATOR = 5;

    /* ClubOk Responses */
    /* Success Responses */
    public static final ClubOkResponse SUCCESS_CREATE = new ClubOkSuccessResponse(1, "Entity successfully created");
    public static final ClubOkResponse SUCCESS_QUERY = new ClubOkSuccessResponse(2, "Query is successful");
    public static final ClubOkResponse SUCCESS_EDIT = new ClubOkSuccessResponse(3, "Entity successfully editted");
    public static final ClubOkResponse SUCCESS_DELETE = new ClubOkSuccessResponse(4, "Entity successfully deleted");
    public static final ClubOkResponse SUCCESS_REGISTRATION = new ClubOkSuccessResponse(5, "Registration successful");
    public static final ClubOkResponse SUCCESS_LOGIN = new ClubOkSuccessResponse(6, "Login successful");
    public static final ClubOkResponse SUCCESS_LOGOUT = new ClubOkSuccessResponse(7, "Logout successful");
    public static final ClubOkResponse SUCCESS_SUBSCRIPTION = new ClubOkSuccessResponse(8, "Subscribing successful");
    public static final ClubOkResponse SUCCESS_UNSUBSCRIPTION = new ClubOkSuccessResponse(9, "Unsubscribing successful");
    public static final ClubOkResponse SUCCESS_MEMBERSHIP = new ClubOkSuccessResponse(10, "Adding new member successful");
    public static final ClubOkResponse SUCCESS_UNMEMBERSHIP = new ClubOkSuccessResponse(11, "Deleting a member successful");

    /* Error Responses */
    public static final ClubOkResponse ERROR_SERVER_UNKNOWN = new ClubOkErrorResponse(50, "Unknown Server Error. See details");
    public static final ClubOkResponse ERROR_VALIDATION = new ClubOkErrorResponse(51, "Validation error");
    public static final ClubOkResponse ERROR_CREATE =  new ClubOkErrorResponse(52, "Couldn't create entity");
    public static final ClubOkResponse ERROR_QUERY = new ClubOkErrorResponse(53, "Query error");
    public static final ClubOkResponse ERROR_DB = new ClubOkErrorResponse(54, "Error accessing database");
    public static final ClubOkResponse ERROR_ILLEGAL_ID = new ClubOkErrorResponse(55, "Invalid ID format");
    public static final ClubOkResponse ERROR_LOGIN = new ClubOkErrorResponse(56, "Couldn't login");
    public static final ClubOkResponse ERROR_LOGOUT = new ClubOkErrorResponse(57, "Couldn't logout");
    public static final ClubOkResponse ERROR_QUERY_PARAMS = new ClubOkErrorResponse(58, "Incorrect query parameters");
    public static final int TOKEN_ERROR = 105;


    /* Http Responses */
    public static Object response(Response response, int statusCode, ClubOkResponse clubOkResponse, Document results) {
        response.status(statusCode);
        response.type(JSON);
        return results.append("response", clubOkResponse);
    }

    public static Object response(Response response, int statusCode, ClubOkResponse clubOkResponse) {
        return response(response, statusCode, clubOkResponse, new Document());
    }
}
