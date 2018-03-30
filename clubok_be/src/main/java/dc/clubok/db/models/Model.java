package dc.clubok.db.models;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;

public interface Model {
    <T extends Entity> void saveOne(T entity, Class<T> type) throws Exception;

    <T extends Entity> void saveMany(List<T> entities, Class<T> type) throws Exception;

    <T extends Entity> long count(Class<T> type);

    <T extends Entity> T findOne(Document document, Class<T> type);

    <T extends Entity> List<T> findAll(Class<T> type);

    <T extends Entity> T findById(String id, Class<T> type);

    <T extends Entity> T findById(ObjectId id, Class<T> type);

    <T extends Entity> T findByField(String fieldName, String value, Class<T> type);

    <T extends Entity> void deleteById(String id, Class<T> type);

    <T extends Entity> void deleteById(ObjectId id, Class<T> type);

    <T extends Entity> void update(T entity, Document update, Class<T> type);

    <T extends Entity> void validate(T entity) throws Exception;
}
