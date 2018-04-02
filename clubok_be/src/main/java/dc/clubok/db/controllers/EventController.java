package dc.clubok.db.controllers;

import dc.clubok.db.models.Event;
import dc.clubok.utils.exceptions.ClubOkException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static dc.clubok.utils.Constants.model;

public class EventController {

    private static Logger logger = LoggerFactory.getLogger(EventController.class.getCanonicalName());

    public static void createEvent(Event event) throws ClubOkException {
        model.saveOne(event, Event.class);
    }

    public static List<Event> getEvents(String params) throws ClubOkException {
//        TODO Add parameter handler
        return model.findAll(Event.class);
    }

    public static Event getEventById(String eventId) throws ClubOkException {
        return model.findById(eventId, Event.class);
    }

    public static void deleteEventById(String eventId) throws ClubOkException {
        model.deleteById(eventId, Event.class);
    }

    public static void editEventById(String eventId, Event event) throws ClubOkException {
        model.update(event, new Document("_id", eventId), Event.class);
    }

}
