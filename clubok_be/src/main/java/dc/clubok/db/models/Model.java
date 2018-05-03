package dc.clubok.db.models;

import dc.clubok.utils.ClubOkException;
import dc.clubok.utils.SearchParams;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;

public interface Model {
    <T extends Entity> void saveOne(T entity, Class<T> type) throws ClubOkException;

    <T extends Entity> void saveMany(List<T> entities, Class<T> type) throws ClubOkException;

    <T extends Entity> long count(Class<T> type) throws ClubOkException;

    <T extends Entity> T findOne(Document document, Class<T> type) throws ClubOkException;

    <T extends Entity> List<T> findMany(int size, int page, String orderBy, String order, Bson include, Bson exclude, Class<T> type) throws ClubOkException;

    <T extends Entity> List<T> findByParams(SearchParams params, Class<T> type) throws ClubOkException;

    <T extends Entity> List<T> findAll(Class<T> type) throws ClubOkException;

    <T extends Entity> T findById(String id, Class<T> type) throws ClubOkException;

    <T extends Entity> T findById(ObjectId id, Class<T> type) throws ClubOkException;

    <T extends Entity> T findByField(String fieldName, String value, Class<T> type) throws ClubOkException;

    <T extends Entity> void deleteById(String id, Class<T> type) throws ClubOkException;

    <T extends Entity> void deleteById(ObjectId id, Class<T> type) throws ClubOkException;

    /* Updating entries in Database */
    <T extends Entity> void updateRaw(Bson query, Bson update, Class<T> type) throws ClubOkException;

    <T extends Entity> void update(T entity, Bson update, Class<T> type) throws ClubOkException;

    <T extends Entity> void modify(T entity, Document update, Class<T> type) throws ClubOkException;

    <T extends Entity, S> void addOneToArray(T entity, String fieldName, S value, Class<T> type) throws ClubOkException;

    <T extends Entity, S> void addManyToArray(T entity, String fieldName, List<S> values, Class<T> type) throws ClubOkException;

    <T extends Entity, S> void addOneToSet(T entity, String fieldName, S value, Class<T> type) throws ClubOkException;

    <T extends Entity, S> void addManyToSet(T entity, String fieldName, List<S> values, Class<T> type) throws ClubOkException;

    <T extends Entity, S extends Entity> void modifyOneFromArray(T entity, String fieldName, S value, Document update, Class<T> type) throws ClubOkException;

    <T extends Entity, S> void removeOneFromArray(T entity, String fieldName, S value, Class<T> type) throws ClubOkException;

    <T extends Entity, S> void removeManyFromArray(T entity, String fieldName, List<S> values, Class<T> type) throws ClubOkException;

    <T extends Entity> void validate(T entity) throws ClubOkException;

}
