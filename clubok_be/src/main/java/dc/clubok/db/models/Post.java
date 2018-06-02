package dc.clubok.db.models;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.*;

@Getter @Setter
public class Post extends Entity {
    public static void setupCollection(MongoDatabase db) {
        String collectionName = Post.class.getSimpleName().toLowerCase() + "s";
        boolean collectionCreated = false;
        for(String name: db.listCollectionNames()) {
            if (name.equals(collectionName)) collectionCreated = true;
        }

        if (!collectionCreated) {
            db.createCollection(collectionName, new CreateCollectionOptions().validationOptions(VALIDATION_OPTIONS));
        }
    }

    private static final ValidationOptions VALIDATION_OPTIONS = new ValidationOptions().validator(
            and(
                    and(
                            exists("club"),
                            exists("user"),
                            exists("body"),
                            exists("type"),
                            exists("postedAt")
                    ),
                    regex("body", ".+"),
                    or(ne("type", "event"), exists("event"))
            )
    );

    private PostClubInfo club;
    private PostUserInfo user;
    private int postedAt;
    private String type;
    private String body;

    private Event event;

    private Set<String> likes;
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

    @Override
    public String toString() {
        return String.format("Post (%s) [%s]: %s", getId().toHexString(), getType(), getBody());
    }
}
