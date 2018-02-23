package dc.clubok.entities;

import lombok.Data;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;

@Data
public abstract class Entity {
    private @NotNull ObjectId id;
}
