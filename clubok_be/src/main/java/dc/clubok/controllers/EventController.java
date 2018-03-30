package dc.clubok.controllers;

import dc.clubok.models.Event;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.*;

import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.*;

public class EventController {

    private static Logger logger = LoggerFactory.getLogger(EventController.class.getCanonicalName());

    public static Route addEvent = (Request request, Response response) -> {
        logger.debug("POST /events " + request.body());
        try {
            Event event = gson.fromJson(request.body(), Event.class);
            model.saveOne(event, Event.class);

            response.type(JSON);
            response.status(SC_OK);
            return event;
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route fetchAllEvents = (Request request, Response response) -> {
        logger.debug("GET /events");

        try {
            response.type(JSON);
            response.status(SC_OK);
            return model.findAll(Event.class);
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route getEventById = (Request request, Response response) -> {
        logger.debug("GET /events/" + request.params(":id"));
        try {
            Event event = model.findById(new ObjectId(request.params(":id")), Event.class);
            if (event == null) {
                response.type(JSON);
                response.status(SC_NOT_FOUND);
                return "";
            }
            response.type(JSON);
            response.status(SC_OK);
            return event;
        } catch (IllegalArgumentException e) {
            response.type(JSON);
            response.status(SC_NOT_FOUND);
            return "";
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

//    public static Route getEventsByClubId = (Request request, Response response) -> {
//        logger.debug("GET /events/" + request.params(":id"));
//        try {
//            List<Event> eventList = model.findAll(Event.class);
//            List<Event> ClubEvents = new ArrayList<>();
//
//            if (eventList.isEmpty()) {
//                response.type(JSON);
//                response.status(SC_NOT_FOUND);
//                return "";
//            }
//            for (Event i: eventList){
//                if (i.getId().toHexString().equals(request.params(":id"))){
//                    ClubEvents.add(i);
//                    System.out.println(i.getTitle());
//                }
//            }
//
//            response.type(JSON);
//            response.status(SC_OK);
//            return ClubEvents;
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

    public static Route deleteEventById = (Request request, Response response) -> {
        logger.debug("GET /events/" + request.params(":id"));
        try {
            model.deleteOne(new Document("_id", new ObjectId(request.params(":id"))), Event.class);
            response.type(JSON);
            response.status(SC_OK);
            return "";
        } catch (IllegalArgumentException e) {
            response.type(JSON);
            response.status(SC_NOT_FOUND);
            return "";
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

    public static Route editEventById = (Request request, Response response) -> {
        logger.debug("GET /events/" + request.params(":id"));
        try {
            Event event = gson.fromJson(request.body(), Event.class);
            if (event == null) {
                response.type(JSON);
                response.status(SC_NOT_FOUND);
                return "";
            }
            model.update(event, new Document("_id", new ObjectId(request.params(":id"))), Event.class);
            response.type(JSON);
            response.status(SC_OK);
            return event;
        } catch (IllegalArgumentException e) {
            response.type(JSON);
            response.status(SC_NOT_FOUND);
            return "";
        } catch (Exception e) {
            response.type(JSON);
            response.status(SC_BAD_REQUEST);
            return e;
        }
    };

}
