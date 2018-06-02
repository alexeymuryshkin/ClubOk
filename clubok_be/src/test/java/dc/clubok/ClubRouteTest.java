package dc.clubok;

import com.google.gson.reflect.TypeToken;
import dc.clubok.db.models.Club;
import dc.clubok.seed.Seed;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static dc.clubok.utils.Constants.gson;
import static dc.clubok.utils.Constants.mongo;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.junit.Assert.*;
import static spark.Spark.stop;

public class ClubRouteTest {
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

    // GET /clubs
    @Test
    public void GetClubs_NoParams_SUCCESS() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/clubs")
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        assertTrue("should return success code, but " + response.getStatusLine().getStatusCode() + " returned",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertSuccess(responseBody);

        assertTrue("response should have result", responseBody.containsKey("result"));

        List<Club> result = getResult(responseBody);
        assertEquals("should return correct number of posts", Seed.clubs.size(), result.size());
    }

    // GET /clubs/:id
    @Test
    public void GetClubsId_CorrectId_SUCCESS() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/clubs/" + Seed.clubs.get(1).getId().toHexString())
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        assertTrue("should return success code, but " + response.getStatusLine().getStatusCode() + " returned",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertSuccess(responseBody);

        Club club = gson.fromJson(gson.toJson(responseBody.get("result")), Club.class);
        assertNotNull("should return club", club);
        assertEquals("should match club id", Seed.clubs.get(1).getId(), club.getId());
    }

    @Test
    public void GetClubsId_InvalidId_NOTFOUND() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/clubs/5a9d3051937f6d2ff45e7836")
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return NOT_FOUND", SC_NOT_FOUND, response.getStatusLine().getStatusCode());

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertError(responseBody);
    }
}
