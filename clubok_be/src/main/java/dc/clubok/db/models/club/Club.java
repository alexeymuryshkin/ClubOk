package dc.clubok.db.models.club;

import dc.clubok.db.models.Entity;
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
    private String logoSrc;
    private String description;
    private Set<ObjectId> moderators;
    private Set<ObjectId> members;
    private Set<ObjectId> subscribers;

    public Club() {
        setId(new ObjectId());
        moderators = new HashSet<>();
        members = new HashSet<>();
        subscribers = new HashSet<>();
    }

    public Club(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
}
