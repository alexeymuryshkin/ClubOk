package dc.clubok.utils;

import com.google.gson.Gson;
import dc.clubok.config.Config;
import dc.clubok.db.MongoHandle;
import dc.clubok.models.Model;
import dc.clubok.mongomodel.MongoModel;

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
}
