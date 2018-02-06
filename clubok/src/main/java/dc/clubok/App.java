package dc.clubok;

import dc.clubok.db.MongoHandle;
import org.bson.Document;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        MongoHandle mongo = new MongoHandle();
        mongo.getCollection("users").insertOne(new Document("asd", "gdsf"));
    }
}
