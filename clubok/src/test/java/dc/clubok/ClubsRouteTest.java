package dc.clubok;

import com.google.gson.Gson;
import dc.clubok.models.Club;
import dc.clubok.models.Model;
import dc.clubok.mongomodel.MongoModel;
import dc.clubok.seed.Seed;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.Validator;
import java.io.IOException;

import static org.junit.Assert.*;

public class ClubsRouteTest {
    private static Validator validator;
    private static Model model;
    private final Gson gson = new Gson();
    private final HttpClient client = HttpClients.createDefault();
    private final String url = "http://localhost:3000/api";

    @BeforeClass
    public static void setUp() {
        ClubOKService.main(new String[0]);
        validator = ClubOKService.validator;
        model = new MongoModel();
    }

    @Before
    public void setDb() {
        ClubOKService.mongo.getDb().drop();
        Seed.populateUsers();
        Seed.populateClubs();
    }

    @Test
    public void PostClubs_ValidData_OK()
            throws IOException{
        String name = "Test Club";
        Club club = new Club(name);

        HttpUriRequest request = RequestBuilder.post(url + "/clubs")
                .setEntity(new StringEntity(gson.toJson(club)))
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("request does not return OK",
                200, response.getStatusLine().getStatusCode());

        Club clubResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), Club.class);

        assertNotNull("club was not returned",
                clubResponse);
        assertNotNull("club does not have id",
                clubResponse.getId());

        Club clubDB = model.findById(clubResponse.getId(), Club.class);

        assertEquals("number of clubs is incorrect",
                3, model.count(Club.class));
        assertNotNull("club is not created",
                clubDB);

    }

}
