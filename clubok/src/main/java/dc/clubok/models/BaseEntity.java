package dc.clubok.models;

import org.bson.Document;
import org.bson.types.ObjectId;

public abstract class BaseEntity {
    private ObjectId id;

    private Long version;

    BaseEntity() {
        super();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
