package dc.clubok;

import dc.clubok.db.models.Club;
import dc.clubok.seed.Seed;
import dc.clubok.utils.exceptions.ClubOkException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bson.types.ObjectId;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static dc.clubok.utils.Constants.*;
import static org.junit.Assert.*;
import static spark.Spark.stop;

public class ClubsRouteTest {
    private final HttpClient client = HttpClients.createDefault();
    private final String url = "http://localhost:3000/api";

    @BeforeClass
    public static void setUp() {
        ClubOKService.main(new String[0]);
    }

    @AfterClass
    public static void finish() {
        stop();
    }

    @Before
    public void setDb() {
        mongo.getDb().drop();
        Seed.populateUsers();
        Seed.populateClubs();
    }

    @Test
    public void PostClubs_ValidData_SUCCESS() throws IOException, ClubOkException {
        String name = "Test Club";
        Club club = new Club(name);

        HttpUriRequest request = RequestBuilder.post(url + "/clubs")
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .setEntity(new StringEntity(gson.toJson(club)))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertTrue("should return success code",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        String id = gson.fromJson(EntityUtils.toString(response.getEntity()), String.class);
        assertTrue("should return valid id", ObjectId.isValid(id));

        Club clubDb = model.findById(id, Club.class);

        assertEquals("db should have correct number of clubs", 3, model.count(Club.class));
        assertNotNull("db should create user entry", clubDb);
        assertEquals("db entry should match returned id", id, clubDb.getId().toHexString());
    }

}
