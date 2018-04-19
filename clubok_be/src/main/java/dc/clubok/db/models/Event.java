package dc.clubok.db.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

@EqualsAndHashCode(callSuper = true)
public @Data class Event
        extends Entity {
    private String clubId;
    private String title;
    private String description;
    private int start;
    private int end;

    public Event(){
        setId(new ObjectId());
    }

    public Event(String id, String title, String description, int start, int end) {
        this();
        setClubId(id);
        setTitle(title);
        setDescription(description);
        setStart(start);
        setEnd(end);
    }
}
