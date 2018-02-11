package dc.clubok.models;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.List;

public @Data class Club {
    private ObjectId id;
    private String title;
    private String description;
    private List<ObjectId> moderators;
    private List<ObjectId> participants;
    private List<ObjectId> subscribers;
}
