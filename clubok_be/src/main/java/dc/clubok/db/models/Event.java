package dc.clubok.db.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
public @Data class Event
        extends Entity {
    private ObjectId clubId;
    private @NotEmpty String name;
    private String description;
    private int start;
    private int end;
    private String location;

    public Event(){
        setId(new ObjectId());
    }

    public Event(ObjectId id, String name, String description, int start, int end) {
        this();
        setClubId(id);
        setName(name);
        setDescription(description);
        setStart(start);
        setEnd(end);
    }
}
