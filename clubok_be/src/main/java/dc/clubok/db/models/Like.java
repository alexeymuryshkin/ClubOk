package dc.clubok.db.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class Like {
    private String id;
    private String imageSrc;
    private String link;

    public Like(User user) {
        setId(user.getId().toHexString());
        setImageSrc(user.getImageSrc());
        setLink(user.getLink());
    }
}
