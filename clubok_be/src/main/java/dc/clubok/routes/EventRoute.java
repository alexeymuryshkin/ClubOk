package dc.clubok.routes;

import dc.clubok.db.controllers.EventController;
import dc.clubok.db.models.Event;
import dc.clubok.utils.ClubOkException;
import dc.clubok.utils.SearchParams;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;

public class EventRoute {
    private static Logger logger = LoggerFactory.getLogger(EventRoute.class.getCanonicalName());

    public static Route GetEvents = (Request request, Response response) -> {
        try {
            SearchParams params = new SearchParams(request.queryMap().toMap());
            List<Event> events = EventController.getEvents(params);

            Document result = new Document("total", events.size())
                    .append("results", events);

            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route PostEvents = (Request request, Response response) -> {
        try {
            Event event = gson.fromJson(request.body(), Event.class);
            EventController.createEvent(event);

            Document result = new Document("event_id", event.getId().toHexString());

            return response(response, SC_CREATED, SUCCESS_CREATE, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route GetEventsId = (Request request, Response response) -> {
        try {
            Event event = EventController.getEventById(request.params(":id"));
            if (event == null) {
                Document details = new Document("details", "Such event does not exist");
                throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
            }

            Document result = new Document("result", event);

            return response(response, SC_OK, SUCCESS_QUERY, result);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route DeleteEventsId = (Request request, Response response) -> {
        try {
            EventController.deleteEventById(request.params(":id"));
            return response(response, SC_NO_CONTENT, SUCCESS_DELETE);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };

    public static Route PatchEventsId = (Request request, Response response) -> {
        try {
            Document update = Document.parse(request.body());
            EventController.editEventById(request.params(":id"), update);

            return response(response, SC_NO_CONTENT, SUCCESS_EDIT);
        } catch (ClubOkException e) {
            logger.error(e.getResponse().getMessage());
            return response(response, e.getStatusCode(), e.getResponse(), e.getDetails());
        } catch (Exception e) {
            logger.error(e.getClass().getSimpleName() + " " + e.getMessage());
            return response(response, SC_INTERNAL_SERVER_ERROR, ERROR_SERVER_UNKNOWN, new Document("details", e.getMessage()));
        }
    };
}
