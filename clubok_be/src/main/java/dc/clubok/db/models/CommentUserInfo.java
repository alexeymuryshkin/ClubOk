package dc.clubok.db.models;

import lombok.Data;

@Data
public class CommentUserInfo {
    private String id;
    private String imageSrc;
    private String link;
    private String fname;
    private String lname;

    public CommentUserInfo(User user) {
        setId(user.getId().toHexString());
        setImageSrc(user.getImageSrc());
        setLink(user.getLink());
        setFname(user.getFname());
        setLname(user.getLname());
    }
}
