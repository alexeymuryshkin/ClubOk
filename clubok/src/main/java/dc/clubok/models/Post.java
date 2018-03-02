package dc.clubok.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import java.util.ArrayList;
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

    private Post() {
        setId(new ObjectId());
        likes = new ArrayList<>();
    }

    public Post(ObjectId clubId, String type, String title, String body) {
        this();

        this.clubId = clubId;
        this.type = type;
        this.title = title;
        this.body = body;
    }
}
