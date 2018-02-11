package dc.clubok.models;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.List;

public @Data class Post {
    private ObjectId id;
    private ObjectId clubId;
    private String type;
    private String title;
    private String body;
    private List<ObjectId> likes;
    private List<Comment> comments;
}
