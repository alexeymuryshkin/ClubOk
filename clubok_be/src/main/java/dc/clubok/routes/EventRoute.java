package dc.clubok.routes;

import dc.clubok.db.controllers.EventController;
import dc.clubok.db.models.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import static dc.clubok.utils.Constants.*;

public class EventRoute {
    private static Logger logger = LoggerFactory.getLogger(EventRoute.class.getCanonicalName());

    public static Route GetEvents = (Request request, Response response) -> {
        logger.debug("GET /events " + request.queryString());
        try {
            return ok(response, EventController.getEvents(request.queryString()));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return badRequest(response, e);
        }
    };

    public static Route PostEvents = (Request request, Response response) -> {
        logger.debug("POST /events " + request.body());

        try {
            EventController.createEvent(gson.fromJson(request.body(), Event.class));
            return created(response);
        } catch (Exception e) {
            return badRequest(response, e);
        }
    };

    public static Route GetEventsId = (Request request, Response response) -> {
        logger.debug("GET /events/" + request.params(":id"));

        try {
            Event event = EventController.getEventById(request.params(":id"));
            if (event == null) {
                return notFound(response);
            }

            return ok(response, event);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return badRequest(response, e);
        }
    };

    public static Route DeleteEventsId = (Request request, Response response) -> {
        logger.debug("DELETE /events/" + request.params(":id"));
        try {
            EventController.deleteEventById(request.params(":id"));
            return noContent(response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return badRequest(response, e);
        }
    };

    public static Route PatchEventsId = (Request request, Response response) -> {
        logger.debug("PATCH /events/" + request.params(":id") + " " + request.body());
        try {
            Event event = gson.fromJson(request.body(), Event.class);
            EventController.editEventById(request.params(":id"), event);
            return noContent(response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return badRequest(response, e);
        }
    };
}
