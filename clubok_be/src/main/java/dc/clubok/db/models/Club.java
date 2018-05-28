package dc.clubok.db.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
public @Data class Club
        extends Entity {
    @NotNull (message = "Name must be specified")
    @NotEmpty (message = "Club name cannot be empty")
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
}
