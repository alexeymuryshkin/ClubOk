package dc.clubok.db.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data @NoArgsConstructor @AllArgsConstructor
public class Membership {
    private ObjectId userId;
    private ObjectId clubId;
    private String role;
    private int permissionLevel;
    private int start;
    private int end;
}
