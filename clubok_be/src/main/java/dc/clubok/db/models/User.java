package dc.clubok.db.models;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ValidationOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.*;
import static dc.clubok.utils.Constants.PL_REGULAR;

@Getter @Setter
public class User extends Entity {
    public static void setupCollection(MongoDatabase db) {
        String collectionName = User.class.getSimpleName().toLowerCase() + "s";
        boolean collectionCreated = false;
        for(String name: db.listCollectionNames()) {
            if (name.equals(collectionName)) collectionCreated = true;
        }

        if (!collectionCreated) {
            IndexOptions indexOptions = new IndexOptions().unique(true);

            db.createCollection(collectionName, new CreateCollectionOptions().validationOptions(VALIDATION_OPTIONS));
            db.getCollection(collectionName).createIndex(Indexes.ascending("email"), indexOptions);
        }
    }

    private static final ValidationOptions VALIDATION_OPTIONS = new ValidationOptions().validator(
            and(
                    and(exists("email"), exists("password"), exists("permissionLevel")),
                    regex("email", "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", "i"),
                    and(lte("permissionLevel", 5), gte("permissionLevel", 0)),
                    or(exists("link", false), regex("link", "[a-z-_]+")),
                    or(exists("imageSrc", false), regex("imageSrc", ".+")),
                    or(exists("fname", false), regex("fname", "[A-Z-]+", "i")),
                    or(exists("lname", false), regex("lname", "[A-Z-]+", "i"))
            )
    );

    private String email;
    private String password;

    private String imageSrc;
    private String link;
    private int permissionLevel;
    private String fname;
    private String lname;

    private Set<String> subscriptions;
    private Set<Membership> membership;
    private List<Token> tokens;

    public User() {
        setId(new ObjectId());
        tokens = new ArrayList<>();
        subscriptions = new HashSet<>();
        membership = new HashSet<>();
        permissionLevel = PL_REGULAR;
    }

    public User(String email, String password) {
        this();
        setEmail(email);
        setPassword(password);
    }

    public User(String email, String password, int permissionLevel) {
        this(email, password);
        setPermissionLevel(permissionLevel);
    }

    @Override
    public String toString() {
        return String.format("User (%s): %s (%s %s)", getId().toHexString(), getEmail(), getFname(), getLname());
    }
}
