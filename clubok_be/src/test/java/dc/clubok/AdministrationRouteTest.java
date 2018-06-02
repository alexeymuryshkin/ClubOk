package dc.clubok;

import com.google.gson.reflect.TypeToken;
import dc.clubok.db.controllers.ClubController;
import dc.clubok.db.models.Club;
import dc.clubok.seed.Seed;
import dc.clubok.utils.ClubOkException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static dc.clubok.utils.Constants.*;
import static org.junit.Assert.*;
import static spark.Spark.stop;

public class AdministrationRouteTest {
    private final HttpClient client = HttpClients.createDefault();
    private final String url = "http://localhost:3000/api/administration";

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
        mongo.setupCollections();
        Seed.populateUsers();
        Seed.populateClubs();
    }

    private void assertSuccess(Document responseBody) {
        assertEquals("should return success status, but returned error (details " + responseBody.getString("details") + ")",
                "success", ((Document) responseBody.get("response")).getString("status"));
    }

    private void assertError(Document responseBody) {
        assertEquals("should return error status, but returned success",
                "error", ((Document) responseBody.get("response")).getString("status"));
    }

    private List<Club> getResult(Document responseBody) {
        return gson.fromJson(gson.toJson(responseBody.get("result")), new TypeToken<List<Club>>(){}.getType());
    }

    // POST /clubs
    @Test
    public void PostClubs_ValidData_SUCCESS() throws IOException, ClubOkException {
        String name = "Test Club";
        Club club = new Club(name);

        HttpUriRequest request = RequestBuilder.post(url + "/clubs")
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .setEntity(new StringEntity(gson.toJson(club)))
                .build();
        HttpResponse response = client.execute(request);

        assertTrue("should return success code, but " + response.getStatusLine().getStatusCode() + " returned",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertSuccess(responseBody);

        assertTrue("response should have clubId", responseBody.containsKey("clubId"));

        String id = responseBody.getString("clubId");
        assertTrue("should return valid id", ObjectId.isValid(id));

        // Assertions in DB
        Club clubDB = ClubController.getClubById(id);
        assertEquals("db should have correct number of clubs", Seed.clubs.size() + 1, model.count(Club.class));
        assertNotNull("db should create user entry", clubDB);
        assertEquals("db entry should match returned id", id, clubDB.getId().toHexString());
    }
}
