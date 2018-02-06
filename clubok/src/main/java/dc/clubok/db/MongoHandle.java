package dc.clubok.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dc.clubok.config.Config;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MongoHandle {
    private MongoDatabase db;

    public MongoHandle() {
        Config config = new Config();

        MongoClientURI uri = new MongoClientURI(config.getProperties().getProperty("mongodb_uri"));
        MongoClient client = new MongoClient(uri);
        db = client.getDatabase(uri.getDatabase());
    }

    public MongoCollection<Document> getCollection(String name) {
        return db.getCollection(name);
    }
}
