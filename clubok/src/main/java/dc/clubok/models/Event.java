package dc.clubok.models;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Date;

public @Data class Event {
    private ObjectId id;
    private String title;
    private String description;
    private Date datetime;
}
