package dc.clubok.db.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class Post
        extends Entity {
    private ObjectId clubId;
    private ObjectId userId;
    private int postedAt;
    private String type;
    private @NotEmpty (message = "Body cannot be empty")
    String body;

    private Event event;

    private Set<ObjectId> likes;
    private List<Comment> comments;
    private List<String> images;

    public Post() {
        setId(new ObjectId());
        images = new ArrayList<>();
        likes = new HashSet<>();
        comments = new ArrayList<>();
    }

    public Post(ObjectId clubId, String type, String body) {
        this();
        setClubId(clubId);
        setPostedAt(getId().getTimestamp());
        setType(type);
        setBody(body);
    }
}
