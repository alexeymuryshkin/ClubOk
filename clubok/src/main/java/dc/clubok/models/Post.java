package dc.clubok.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
public @Data class Post
        extends Entity {
    private ObjectId clubId;
    private String type;
    private String title;
    private String body;
    private List<ObjectId> likes;
    private List<Comment> comments;
}
