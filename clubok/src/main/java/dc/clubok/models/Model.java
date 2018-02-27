package dc.clubok.models;

import org.bson.Document;
import org.bson.types.ObjectId;
import spark.Request;
import spark.Response;

import java.util.List;

public interface Model {
    <T extends Entity> void save(T entity, Class<T> type) throws Exception;

    <T extends Entity> void saveMany(List<T> entities, Class<T> type) throws Exception;

    <T extends Entity> long count(Class<T> type);

    <T extends Entity> T findById(ObjectId id, Class<T> c);

    <T extends Entity> void update(T entity, Document update, Class<T> type);

    <T extends Entity> void validate(T entity) throws Exception;

    User findByEmail(String email);

    User findByCredentials(String email, String password)
            throws NullPointerException;

    User findByToken(String token);

    void removeToken(User user, String headers);

    boolean authenticate(Request req, Response res);
}
