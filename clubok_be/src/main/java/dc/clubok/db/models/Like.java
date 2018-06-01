package dc.clubok.db.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class Like {
    private String id;
    private String imageSrc;

    public Like(User user) {
        setId(user.getId().toHexString());
        setImageSrc(user.getImageSrc());
    }
}
