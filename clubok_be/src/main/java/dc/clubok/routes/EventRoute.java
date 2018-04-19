package dc.clubok.routes;

import dc.clubok.db.controllers.EventController;
import dc.clubok.db.models.Event;
import dc.clubok.utils.exceptions.ClubOkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import static dc.clubok.utils.Constants.gson;
import static dc.clubok.utils.Constants.response;
import static org.apache.http.HttpStatus.*;

public class EventRoute {
    private static Logger logger = LoggerFactory.getLogger(EventRoute.class.getCanonicalName());

    public static Route GetEvents = (Request request, Response response) -> {
        try {
            if (request.params("start") == null) {
                logger.debug("start is null");
            }
            return response(response, SC_OK, EventController.getAllEvents());
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
