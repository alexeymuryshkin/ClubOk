package dc.clubok.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
public @Data class Comment
        extends Entity{
    private ObjectId userId;
    private String text;
    private Date lastEdited;
}
