package dc.clubok.utils;

import com.google.gson.Gson;
import dc.clubok.config.Config;
import dc.clubok.db.handlers.MongoHandle;
import dc.clubok.db.models.Model;
import dc.clubok.db.mongomodel.MongoModel;
import spark.Response;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.apache.http.HttpStatus.*;

public class Constants {
    public static final Config config = new Config();

    /* Content types */
    public static final String JSON = "application/json";

    /* Mongo */
    public static final MongoHandle mongo = new MongoHandle();
    public static final Model model = new MongoModel();

    public static final Gson gson = new Gson();
    public static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static Object ok(Response response) {
        response.status(SC_OK);
        response.type(JSON);
        return "";
    }

    public static Object ok(Response response, Object o) {
        response.status(SC_OK);
        response.type(JSON);
        return o;
    }

    public static Object created(Response response) {
        response.status(SC_CREATED);
        response.type(JSON);
        return "";
    }

    public static Object created(Response response, Object o) {
        response.status(SC_CREATED);
        response.type(JSON);
        return o;
    }

    public static Object noContent(Response response) {
        response.status(SC_NO_CONTENT);
        return "";
    }

    public static Object badRequest(Response response, Object o) {
        response.status(SC_BAD_REQUEST);
        response.type(JSON);
        return o;
    }

    public static Object notFound(Response response) {
        response.status(SC_NOT_FOUND);
        response.type(JSON);
        return "";
    }
    public static Object notFound(Response response, Object o) {
        response.status(SC_NOT_FOUND);
        response.type(JSON);
        return o;
    }
}
