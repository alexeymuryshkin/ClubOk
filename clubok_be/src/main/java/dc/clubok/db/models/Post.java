package dc.clubok.db.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class Post
        extends Entity {
    @NotNull (message = "Club must be specified")
    private PostClubInfo club;
    @NotNull (message = "User must be specified")
    private PostUserInfo user;
    private int postedAt;
    private String type;
    @NotNull (message = "Body must be specified")
    @NotEmpty (message = "Body cannot be empty")
    private String body;

    private Event event;

    private Set<Like> likes;
    private List<Comment> comments;
    private List<String> images;

    public Post() {
        setId(new ObjectId());
        images = new ArrayList<>();
        likes = new HashSet<>();
        comments = new ArrayList<>();
    }

    public Post(Club club, User user, String type, String body) {
        this();
        setClub(new PostClubInfo(club));
        setUser(new PostUserInfo(user));
        setPostedAt(getId().getTimestamp());
        setType(type);
        setBody(body);
    }
}
