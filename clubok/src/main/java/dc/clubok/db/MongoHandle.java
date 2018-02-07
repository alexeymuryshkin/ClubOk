package dc.clubok.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dc.clubok.App;
import org.bson.Document;

import java.util.Properties;

public class MongoHandle {
    private MongoDatabase db;

    public MongoHandle() {
        Properties prop = App.config.getProperties();
        MongoClientURI uri = new MongoClientURI(prop.getProperty("mongodb_uri"));
        MongoClient client = new MongoClient(uri);
        db = client.getDatabase(uri.getDatabase());
    }

    public MongoCollection<Document> getCollection(String name) {
        return db.getCollection(name);
    }
}
