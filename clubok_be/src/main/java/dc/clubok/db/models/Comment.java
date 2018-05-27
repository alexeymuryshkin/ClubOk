package dc.clubok.db.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

@EqualsAndHashCode(callSuper = true)
public @Data class Comment
        extends Entity {
    private CommentUserInfo user;
    private String text;

    public Comment() {
        this.setId(new ObjectId());
    }
}
