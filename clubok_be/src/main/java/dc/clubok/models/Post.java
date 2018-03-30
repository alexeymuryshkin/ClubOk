package dc.clubok.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Post
        extends Entity {
    private @NotEmpty (message = "Club Id is not specified")
    String clubId;
    private @NotEmpty (message = "User Id is not specified")
    String userId;
    private String type;
    private @NotEmpty (message = "Title cannot be null")
    String title;
    private @NotEmpty (message = "Body cannot be null")
    String body;
    private int lastModified;
    private List<String> likes;
    private List<Comment> comments;

    public Post() {
        setId(new ObjectId());
        likes = new ArrayList<>();
        comments = new ArrayList<>();
        lastModified = getId().getTimestamp();
    }

    public Post(String clubId, String type, String title, String body) {
        this();
        this.clubId = clubId;
        this.type = type;
        this.title = title;
        this.body = body;
    }
}
