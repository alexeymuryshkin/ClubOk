package dc.clubok.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public @Data class Club
        extends Entity{
    @NotNull @NotEmpty
    private String name;
    private String logoSrc;
    private String description;
    private List<ObjectId> moderators;
    private List<ObjectId> participants;
    private List<ObjectId> subscribers;
}
