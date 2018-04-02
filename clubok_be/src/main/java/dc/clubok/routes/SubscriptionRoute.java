package dc.clubok.routes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import static dc.clubok.utils.Constants.response;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

public class SubscriptionRoute {
    private static Logger logger = LoggerFactory.getLogger(SubscriptionRoute.class.getCanonicalName());

    public static Route PostSubscriptions = (Request request, Response response) -> {
        logger.debug("POST /subscriptions " + request.body());
//        TODO
        return response(response, SC_NOT_FOUND);
    };

    public static Route DeleteSubscriptions = (Request request, Response response) -> {
        logger.debug("DELETE /subscriptions " + request.body());
//        TODO
        return response(response, SC_NOT_FOUND);
    };

}
