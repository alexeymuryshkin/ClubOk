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
    private @NotEmpty(message = "Club Id cannot be empty")
    String clubId;
    private @NotEmpty (message = "User Id cannot be empty")
    String userId;
    private String type;
    private @NotEmpty (message = "Title cannot be empty")
    String title;
    private @NotEmpty (message = "Body cannot be empty")
    String body;
    private Set<String> likes;
    private List<Comment> comments;

    public Post() {
        setId(new ObjectId());
        likes = new HashSet<>();
        comments = new ArrayList<>();
    }

    public Post(String clubId, String type, String title, String body) {
        this();
        this.clubId = clubId;
        this.type = type;
        this.title = title;
        this.body = body;
    }
}
