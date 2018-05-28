package dc.clubok.db.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class PostClubInfo {
    private String id;
    private String imageSrc;
    private String name;
    private String link;

    public PostClubInfo(Club club) {
        setId(club.getId().toHexString());
        setImageSrc(club.getImageSrc());
        setName(club.getName());
    }
}
