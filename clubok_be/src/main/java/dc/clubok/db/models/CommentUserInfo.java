package dc.clubok.db.models;

import lombok.Data;

@Data
public class CommentUserInfo {
    private String id;
    private String fname;
    private String lname;

    public CommentUserInfo(User user) {
        setId(user.getId().toHexString());
        setFname(user.getFname());
        setLname(user.getLname());
    }
}
