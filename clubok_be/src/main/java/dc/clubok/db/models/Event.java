package dc.clubok.db.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
public @Data class Event
        extends Entity {
    private EventClubInfo club;
    private @NotEmpty String name;
    private String description;
    private int start;
    private int end;
    private String location;

    public Event(){
        setId(new ObjectId());
    }

    public Event(Club club, String name, String description, int start, int end) {
        this();
        setClub(new EventClubInfo(club));
        setName(name);
        setDescription(description);
        setStart(start);
        setEnd(end);
    }
}
