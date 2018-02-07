package dc.clubok.data;

import com.mongodb.client.MongoCollection;
import dc.clubok.db.MongoHandle;
import dc.clubok.models.UserEntity;
import org.bson.Document;
import org.bson.json.JsonParseException;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;


public class MongoUserDB implements UserDB {
    private static MongoCollection<Document> collection;

    public MongoUserDB() {
        MongoHandle mongo = new MongoHandle();
        collection = mongo.getCollection("users");
    }

    @Override
    public void add(UserEntity user) {

        // Password encryption
        String hashedPassword = Crypt.hash(user.getPassword().toCharArray());

        Document userDocument = new Document("email", user.getEmail())
                .append("password", hashedPassword)
                .append("fname", user.getFname())
                .append("lname", user.getLname());

        collection.insertOne(userDocument);
        System.out.println("User has been created");
    }

    @Override
    public void add(List<UserEntity> users) {
//        TODO Complete add many implementation
    }

    @Override
    public void delete(UserEntity user) {
//        TODO Complete delete implementation
    }

    @Override
    public UserEntity findById(String id) {
//        TODO Complete findById implementation
        return null;
    }

    @Override
    public UserEntity findByCredentials(String email, String password)
            throws NullPointerException, IllegalArgumentException {
        System.out.println("Searching user by credentials: " + email + ":" + password);
        Document userDocument = collection.find(eq("email", email)).first();

        if (userDocument == null)
            throw new NullPointerException();

        UserEntity user = UserEntity.fromDocument(userDocument);


        if (Crypt.compare(password.toCharArray(), user.getPassword()))
            return user;
        else
            throw new IllegalArgumentException();
    }
}
