package dc.clubok.db.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public @Data class Club
        extends Entity{
    @NotNull @NotEmpty
    private String name;
    private String logoSrc;
    private String description;
    private List<String> moderators;
    private List<String> participants;
    private List<String> subscribers;

    public Club() {
        setId(new ObjectId());
        moderators = new ArrayList<>();
        participants = new ArrayList<>();
        subscribers = new ArrayList<>();
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
