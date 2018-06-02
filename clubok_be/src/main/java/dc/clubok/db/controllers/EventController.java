package dc.clubok.db.controllers;

import dc.clubok.db.models.Event;
import dc.clubok.utils.ClubOkException;
import dc.clubok.utils.SearchParams;
import org.bson.Document;

import java.util.List;

import static dc.clubok.utils.Constants.ERROR_QUERY;
import static dc.clubok.utils.Constants.model;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

public class EventController {
    public static void createEvent(Event event) throws ClubOkException {
        model.saveOne(event, Event.class);
    }

    public static void createManyEvents(List<Event> events) throws ClubOkException {
        model.saveMany(events, Event.class);
    }

    public static List<Event> getEvents(SearchParams params) throws ClubOkException {
        return model.findByParams(params, Event.class);
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
        Event event = getEventById(eventId);
        if (event == null) {
            Document details = new Document("details", "Such event does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }

        model.deleteById(eventId, Event.class);
    }

    public static void editEventById(String eventId, Document update) throws ClubOkException {
        Event event = getEventById(eventId);
        if (event == null) {
            Document details = new Document("details", "Such event does not exist");
            throw new ClubOkException(ERROR_QUERY, details, SC_NOT_FOUND);
        }

        model.modify(event, update, Event.class);
    }
}
