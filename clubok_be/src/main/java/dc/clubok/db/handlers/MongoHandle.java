package dc.clubok.db.handlers;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import dc.clubok.db.models.Club;
import dc.clubok.db.models.Event;
import dc.clubok.db.models.Post;
import dc.clubok.db.models.User;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.Properties;

import static dc.clubok.utils.Constants.config;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoHandle {
    private MongoDatabase db;

    public MongoHandle() {
        Properties prop = config.getProperties();
        MongoClientURI uri = new MongoClientURI(prop.getProperty("mongodb_uri"));
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClient client = new MongoClient(uri);
        db = client.getDatabase(uri.getDatabase()).withCodecRegistry(pojoCodecRegistry);

        setupCollections();
    }

    public void setupCollections() {
        User.setupCollection(this.db);
        Club.setupCollection(this.db);
        Post.setupCollection(this.db);
        Event.setupCollection(this.db);
    }

    public MongoDatabase getDb() {
        return db;
    }
}
