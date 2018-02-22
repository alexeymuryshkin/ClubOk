package dc.clubok.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import dc.clubok.ClubOKService;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.Properties;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoHandle {
    private MongoDatabase db;

    public MongoHandle() {
        Properties prop = ClubOKService.config.getProperties();
        MongoClientURI uri = new MongoClientURI(prop.getProperty("mongodb_uri"));
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClient client = new MongoClient(uri);
        db = client.getDatabase(uri.getDatabase()).withCodecRegistry(pojoCodecRegistry);
    }

    public MongoDatabase getDb() {
        return db;
    }
}
