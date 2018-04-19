package dc.clubok.db.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
public @Data class Event
        extends Entity {
    private String ClubId;
    private String title;
    private String description;
    private Date datetime;

    public Event(){
        setId(new ObjectId());
    }

    public Event(String id, String title, String description, Date datetime) {
        this();
        setClubId(id);
        setTitle(title);
        setDescription(description);
        setDatetime(datetime);
    }
}
