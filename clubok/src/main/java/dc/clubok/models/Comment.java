package dc.clubok.models;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Date;

public @Data class Comment {
    private ObjectId userId;
    private String text;
    private Date lastEdited;
}
