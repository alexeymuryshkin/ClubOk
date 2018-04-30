package dc.clubok.db.models.post;

import dc.clubok.db.models.Entity;
import dc.clubok.db.models.event.Event;
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
    private @NotEmpty(message = "Club Id cannot be empty")
    String clubId;
    private @NotEmpty (message = "User Id cannot be empty")
    String userId;
    private int postedAt;
    private String type;
    private @NotEmpty (message = "Body cannot be empty")
    String body;
    private List<String> images;
    private Event event;
    private Set<String> likes;
    private List<Comment> comments;

    public Post() {
        setId(new ObjectId());
        images = new ArrayList<>();
        likes = new HashSet<>();
        comments = new ArrayList<>();
    }

    public Post(String clubId, String type, String body) {
        this();
        setClubId(clubId);
        setPostedAt(getId().getTimestamp());
        setType(type);
        setBody(body);
    }
}
