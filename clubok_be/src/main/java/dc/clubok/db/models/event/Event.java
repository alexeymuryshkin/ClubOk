package dc.clubok.db.models.event;

import dc.clubok.db.models.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
public @Data class Event
        extends Entity {
    private @NotEmpty (message = "Club Id cannot be empty") String clubId;
    private @NotEmpty String name;
    private String description;
    private int start;
    private int end;
    private String location;

    public Event(){
        setId(new ObjectId());
    }

    public Event(String id, String name, String description, int start, int end) {
        this();
        setClubId(id);
        setName(name);
        setDescription(description);
        setStart(start);
        setEnd(end);
    }
}
