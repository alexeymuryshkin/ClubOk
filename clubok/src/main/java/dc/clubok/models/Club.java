package dc.clubok.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
public @Data class Club
        extends Entity{
    private String title;
    private String description;
    private List<ObjectId> moderators;
    private List<ObjectId> participants;
    private List<ObjectId> subscribers;
}
