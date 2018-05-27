package dc.clubok.db.models;

import lombok.Data;

@Data
public class EventClubInfo {
    private String id;
    private String name;
    private String imageSrc;

    public EventClubInfo(Club club) {
        setId(club.getId().toHexString());
        setName(club.getName());
        setImageSrc(club.getImageSrc());
    }
}
