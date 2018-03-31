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
        extends Entity{
    @NotNull @NotEmpty
    private String name;
    private String logoSrc;
    private String description;
    private Set<ObjectId> moderators;
    private Set<ObjectId> participants;
    private Set<ObjectId> subscribers;

    public Club() {
        setId(new ObjectId());
        moderators = new HashSet<>();
        participants = new HashSet<>();
        subscribers = new HashSet<>();
    }

    public Club(String name) {
        this();
        this.name = name;
    }

    public Club(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
