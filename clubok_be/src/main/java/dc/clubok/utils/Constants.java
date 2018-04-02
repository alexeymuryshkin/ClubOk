package dc.clubok.utils;

import com.google.gson.Gson;
import dc.clubok.config.Config;
import dc.clubok.db.handlers.MongoHandle;
import dc.clubok.db.models.Model;
import dc.clubok.db.mongomodel.MongoModel;
import spark.Response;

import javax.validation.Validation;
import javax.validation.Validator;

public class Constants {
    public static final Config config = new Config();

    /* Content types */
    public static final String JSON = "application/json";

    /* Mongo */
    public static final MongoHandle mongo = new MongoHandle();
    public static final Model model = new MongoModel();

    public static final Gson gson = new Gson();
    public static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /* Error Codes */
    public static final int VALIDATION_ERROR = 101;

    public static final int DB_SAVE_ERROR = 102;
    public static final int DB_ERROR = 102;
    public static final int DB_QUERY_ERROR = 103;
    public static final int DB_UPDATE_ERROR = 104;
    public static final int LOGIN_ERROR = 105;
    public static final int USER_NOT_FOUND = 105;
    public static final int POST_NOT_FOUND = 105;
    public static final int INCORRECT_USER_ID = 105105105;
    public static final int LOGOUT_ERROR = 105105105;


    /* Http Responses */
    public static Object response(Response response, int statusCode, Object o) {
        response.status(statusCode);
        response.type(JSON);
        return o;
    }

    public static Object response(Response response, int statusCode) {
        return response(response, statusCode, "");
    }
}
