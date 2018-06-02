package dc.clubok.db.models;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter @Setter
public abstract class Entity {
    private ObjectId id;
}
