package dc.clubok.models;

import org.bson.Document;
import org.bson.types.ObjectId;
import spark.Request;
import spark.Response;

import java.util.List;

public interface UserModel {
    void save(User user) throws Exception;

    void saveMany(List<User> users) throws Exception;

    long count();

    User findById(ObjectId id);

    User findByEmail(String email);

    User findByCredentials(String email, String password);

    User findByToken(String token);

    User authenticate(Request req, Response res);

    void update(User user, Document update);
}
