package dc.clubok.db.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Membership {
    private String userId;
    private String clubId;
    private String role;
    private int permissionLevel;
    private int start;
    private int end;
}
