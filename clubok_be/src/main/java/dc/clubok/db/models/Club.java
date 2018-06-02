package dc.clubok.db.models;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.*;

@Getter @Setter
public class Club extends Entity {
    public static void setupCollection(MongoDatabase db) {
        String collectionName = Club.class.getSimpleName().toLowerCase() + "s";
        boolean collectionCreated = false;
        for(String name: db.listCollectionNames()) {
            if (name.equals(collectionName)) collectionCreated = true;
        }

        if (!collectionCreated) {
            IndexOptions indexOptions = new IndexOptions().unique(true);
            List<IndexModel> indexes = Arrays.asList(
                    new IndexModel(Indexes.ascending("name"), indexOptions),
                    new IndexModel(Indexes.ascending("link"), indexOptions)
            );

            db.createCollection(collectionName, new CreateCollectionOptions().validationOptions(VALIDATION_OPTIONS));
            db.getCollection(collectionName).createIndexes(indexes);
        }
    }

    private static final ValidationOptions VALIDATION_OPTIONS = new ValidationOptions().validator(
            and(
                    exists("name"),
                    regex("name", ".+"),
                    or((exists("link", false)), regex("link", "[a-z-_]+")),
                    or((exists("imageSrc", false)), regex("imageSrc", ".+")),
                    or((exists("description", false)), regex("description", ".+"))
            )
    );

    private String name;
    private String imageSrc;
    private String description;
    private String link;

    private Set<Event> events;
    private Set<Membership> members;
    private Set<String> subscribers;

    public Club() {
        setId(new ObjectId());
        events = new HashSet<>();
        members = new HashSet<>();
        subscribers = new HashSet<>();
    }

    public Club(String name) {
        this();
        setName(name);
    }

    public Club(String name, String description) {
        this(name);
        setDescription(description);
    }

    @Override
    public String toString() {
        return String.format("Club (%s): %s", getId().toHexString(), getName());
    }
}
