package dc.clubok.routes;

import dc.clubok.db.controllers.EventController;
import dc.clubok.db.models.Event;
import dc.clubok.utils.exceptions.ClubOkException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.include;
import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;

public class EventRoute {
    private static Logger logger = LoggerFactory.getLogger(EventRoute.class.getCanonicalName());

    public static Route GetEvents = (Request request, Response response) -> {
        try {
            int size = request.queryParams("size") == null ? 50 : Integer.parseInt(request.queryParams("size"));
            int page = request.queryParams("page") == null ? 1 : Integer.parseInt(request.queryParams("page"));
            String orderBy = request.queryParams("orderBy") == null ? "" : request.queryParams("orderBy");
            String order = request.queryParams("order") == null ? "ascending" : request.queryParams("order");

            Bson include = include();
            Bson exclude = exclude();

            Document document = new Document("total", model.count(Event.class))
                    .append("results", EventController.getEvents(size, page, orderBy, order, include, exclude));
            return response(response, SC_OK, document);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route PostEvents = (Request request, Response response) -> {
        logger.debug("POST /events " + request.body());

        try {
            Event event = gson.fromJson(request.body(), Event.class);
            EventController.createEvent(event);
            return response(response, SC_CREATED);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route GetEventsId = (Request request, Response response) -> {
        logger.debug("GET /events/" + request.params(":id"));

        try {
            Event event = EventController.getEventById(request.params(":id"));
            if (event == null) {
                return response(response, SC_NOT_FOUND);
            }

            return response(response, SC_OK, event);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route DeleteEventsId = (Request request, Response response) -> {
        logger.debug("DELETE /events/" + request.params(":id"));
        try {
            EventController.deleteEventById(request.params(":id"));
            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };

    public static Route PatchEventsId = (Request request, Response response) -> {
        logger.debug("PATCH /events/" + request.params(":id") + " " + request.body());
        try {
            Event event = gson.fromJson(request.body(), Event.class);
            EventController.editEventById(request.params(":id"), event);
            return response(response, SC_NO_CONTENT);
        } catch (ClubOkException e) {
            logger.error(e.getMessage());
            return response(response, e.getStatusCode(), e.getError());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, e);
        }
    };
}
