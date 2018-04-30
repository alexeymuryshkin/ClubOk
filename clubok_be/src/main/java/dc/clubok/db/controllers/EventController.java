package dc.clubok.db.controllers;

import dc.clubok.db.models.event.Event;
import dc.clubok.utils.exceptions.ClubOkException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static dc.clubok.utils.Constants.model;

public class EventController {

    private static Logger logger = LoggerFactory.getLogger(EventController.class.getCanonicalName());

    public static void createEvent(Event event) throws ClubOkException {
        model.saveOne(event, Event.class);
    }

    public static List<Event> getEvents(int size, int page, String orderBy, String order, Bson include, Bson exclude) throws ClubOkException {
        return model.findMany(size, page, orderBy, order, include, exclude, Event.class);
    }

    public static Event getEventById(String eventId) throws ClubOkException {
        return model.findById(eventId, Event.class);
    }

//    public static List<Event> getEventsInRange(int start, int end) throws ClubOkException {
//        List <Event> events = model.findAll(Event.class);
//        List <Event> selected = new ArrayList<>();
//        for (Event i: events){
//            Date current = i.getDatetime();
//            if (!current.before(start) && !current.after(end))
//                selected.add(i);
//        }
//        return selected;
//    }

    public static void deleteEventById(String eventId) throws ClubOkException {
        model.deleteById(eventId, Event.class);
    }

    public static void editEventById(String eventId, Event event) throws ClubOkException {
        model.modify(event, new Document("_id", eventId), Event.class);
    }
}
