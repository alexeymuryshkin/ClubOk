package dc.clubok.db.models;

import dc.clubok.utils.exceptions.ClubOkException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;

public interface Model {
    <T extends Entity> void saveOne(T entity, Class<T> type) throws ClubOkException;

    <T extends Entity> void saveMany(List<T> entities, Class<T> type) throws ClubOkException;

    <T extends Entity> long count(Class<T> type) throws ClubOkException;

    <T extends Entity> T findOne(Document document, Class<T> type) throws ClubOkException;

    <T extends Entity> List<T> findAll(Class<T> type) throws ClubOkException;

    <T extends Entity> T findById(String id, Class<T> type) throws ClubOkException;

    <T extends Entity> T findById(ObjectId id, Class<T> type) throws ClubOkException;

    <T extends Entity> T findByField(String fieldName, String value, Class<T> type) throws ClubOkException;

    <T extends Entity> void deleteById(String id, Class<T> type) throws ClubOkException;

    <T extends Entity> void deleteById(ObjectId id, Class<T> type) throws ClubOkException;

    <T extends Entity> void update(Document query, Document command, Class<T> type) throws ClubOkException;

    <T extends Entity> void update(T entity, Bson update, Class<T> type) throws ClubOkException;

    <T extends Entity> void validate(T entity) throws ClubOkException;

}
