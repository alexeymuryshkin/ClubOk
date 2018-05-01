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
    @NotNull @NotEmpty
    private String name;
    private String imageSrc;
    private String description;

    private Set<Event> events;
    private Set<Membership> members;
    private Set<ObjectId> subscribers;

    public Club() {
        setId(new ObjectId());
        events = new HashSet<>();
        members = new HashSet<>();
        subscribers = new HashSet<>();
    }

    public Club(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
}
