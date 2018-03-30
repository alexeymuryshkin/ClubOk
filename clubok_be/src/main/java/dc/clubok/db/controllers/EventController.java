package dc.clubok.db.controllers;

import dc.clubok.db.models.Event;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static dc.clubok.utils.Constants.model;

public class EventController {

    private static Logger logger = LoggerFactory.getLogger(EventController.class.getCanonicalName());

    public static void createEvent(Event event) throws Exception {
        model.saveOne(event, Event.class);
    }

    public static List<Event> getEvents(String params) throws Exception {
        return model.findAll(Event.class);
    }

    public static Event getEventById(String id) {
        return model.findById(id, Event.class);
    }

//    public static void getEventsByClubId (String id) {
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

    public static void deleteEventById(String id) throws Exception {
        model.deleteById(id, Event.class);
    }

    public static void editEventById(String id, Event event) throws Exception {
        model.update(event, new Document("_id", id), Event.class);
    }

}
