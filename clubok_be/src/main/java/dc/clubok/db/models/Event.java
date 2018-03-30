package dc.clubok.db.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
public @Data class Event
        extends Entity {
    private String title;
    private String description;
    private Date datetime;

    public Event(){
        setId(new ObjectId());
    }

    public Event(String title, String description, Date datetime) {
        this();
        setTitle(title);
        setDescription(description);
        setDatetime(datetime);
    }
}
