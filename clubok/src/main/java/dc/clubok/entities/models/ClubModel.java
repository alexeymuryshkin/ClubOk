package dc.clubok.entities.models;

import dc.clubok.entities.Club;

import javax.swing.text.Document;
import java.util.List;

public interface ClubModel {
    void save(Club club) throws Exception;

    void saveMany(List<Club> clubs) throws Exception;

    void update(Club club, Document update);

    long count();

    void validate(Club club) throws Exception;
}
