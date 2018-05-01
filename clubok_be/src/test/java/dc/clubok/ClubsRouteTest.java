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
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
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

    // POST /clubs
    @Test
    public void PostClubs_ValidData_SUCCESS() throws IOException, ClubOkException {
        String name = "Test Club";
        Club club = new Club(name, "description");

        HttpUriRequest request = RequestBuilder.post(url + "/clubs")
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .setEntity(new StringEntity(gson.toJson(club)))
                .build();
        HttpResponse response = client.execute(request);

        assertTrue("should return success status",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        String clubId = gson.fromJson(EntityUtils.toString(response.getEntity()), String.class);

        assertNotNull("club does not have id", clubId);
        assertNotNull("club does not exist", model.findById(clubId, Club.class));
        assertEquals("club name is not same",
                name, model.findById(clubId, Club.class).getName());

        Club clubDB = ClubController.getClubByName(name);

        assertEquals("number of clubs is incorrect",
                3, model.count(Club.class));
        assertNotNull("club is not created", clubDB);
    }

    // GET /clubs
    @Test
    public void GetClubs_SUCCESS() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/clubs")
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        assertTrue("should return success status",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        List<Club> usersResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), new TypeToken<List<Club>>(){}.getType());
        assertEquals("number of users is incorrect",
                2, usersResponse.size());
    }

    // GET /clubs/:id
    @Test
    public void GetClubsId_CorrectId_SUCCESS()
            throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/clubs/" + Seed.clubs.get(1).getId().toHexString())
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        assertTrue("should return success status",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        Club clubResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), Club.class);
        assertNotNull("club is not returned",
                clubResponse);
        assertEquals("club has incorrect id",
                Seed.clubs.get(1).getId(), clubResponse.getId());
    }

    @Test
    public void GetClubsId_IncorrectId_NOTFOUND()
            throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/clubs/5a9d3051937f6d2ff45e7836")
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("should return NOT FOUND", SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    }
}
