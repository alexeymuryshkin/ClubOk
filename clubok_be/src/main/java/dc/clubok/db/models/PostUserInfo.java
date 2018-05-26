package dc.clubok.db.models;

import lombok.Data;

@Data
public class PostUserInfo {
    private String id;
    private String fname;
    private String lname;

    public PostUserInfo(User user) {
        setId(user.getId().toHexString());
        setFname(user.getFname());
        setLname(user.getLname());
    }
}
