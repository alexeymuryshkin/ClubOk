package dc.clubok.db.models;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ValidationOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.*;

@Getter @Setter
public class Event extends Entity {
    public static void setupCollection(MongoDatabase db) {
        String collectionName = Club.class.getSimpleName().toLowerCase() + "s";
        boolean collectionCreated = false;
        for(String name: db.listCollectionNames()) {
            if (name.equals(collectionName)) collectionCreated = true;
        }

        if (!collectionCreated) {
            IndexOptions indexOptions = new IndexOptions().unique(true);

            db.createCollection(collectionName, new CreateCollectionOptions().validationOptions(VALIDATION_OPTIONS));
            db.getCollection(collectionName).createIndex(Indexes.ascending("name"), indexOptions);
        }
    }

    private static final ValidationOptions VALIDATION_OPTIONS = new ValidationOptions().validator(
            and(
                    and(exists("name"), exists("club")),
                    regex("name", ".+"),
                    or(exists("description", false), regex("description", ".+")),
                    or(exists("start", false), regex("start", ".+")),
                    or(exists("end", false), regex("end", ".+")),
                    or(exists("location", false), regex("location", ".+"))
            )
    );

    private EventClubInfo club;
    private String name;
    private String description;
    private int start;
    private int end;
    private String location;

    public Event(){
        setId(new ObjectId());
    }

    public Event(Club club, String name, String description, int start, int end) {
        this();
        setClub(new EventClubInfo(club));
        setName(name);
        setDescription(description);
        setStart(start);
        setEnd(end);
    }

    @Override
    public String toString() {
        return String.format("Event (%s): %s by %s", getId().toHexString(), getName(), getClub().getName());
    }
}
