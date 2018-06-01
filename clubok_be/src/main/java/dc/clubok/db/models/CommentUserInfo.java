package dc.clubok.db.models;

import lombok.Data;

@Data
public class CommentUserInfo {
    private String id;
    private String imageSrc;
    private String fname;
    private String lname;

    public CommentUserInfo() {}

    public CommentUserInfo(User user) {
        setId(user.getId().toHexString());
        setImageSrc(user.getImageSrc());
        setFname(user.getFname());
        setLname(user.getLname());
    }
}
