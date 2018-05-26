package dc.clubok.db.models;

import lombok.Data;

@Data
public class PostClubInfo {
    private String id;
    private String imageSrc;
    private String name;

    public PostClubInfo(Club club) {
        setId(club.getId().toHexString());
        setImageSrc(club.getImageSrc());
        setName(club.getName());
    }
}
