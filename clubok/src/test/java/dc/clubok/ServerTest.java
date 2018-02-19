package dc.clubok;

import com.google.gson.Gson;
import dc.clubok.models.Token;
import dc.clubok.models.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ServerTest {
    private Gson gson = new Gson();
    private String url = "http://localhost:3000";

    @Before
    public void setUp() throws Exception {
        App.main(new String[0]);
        App.mongo.getDb().drop();
    }

    @Test
    public void validData() throws IOException {
        String email = "testmail@example.com";
        String password = "testPass";

        User user = new User(email, password);

        // REQUEST
        // Initialization
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url + "/users");

        // Headers
        post.setHeader("content-type", "application/json");

        // Body
        StringEntity stringEntity = new StringEntity(gson.toJson(user));
        post.setEntity(stringEntity);

        // Sending request
        HttpResponse response = client.execute(post);

        // RESPONSE
        // Fetching user
        User userResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), User.class);

        assertEquals("request does not return OK status",
                200, response.getStatusLine().getStatusCode());
        assertTrue("authorization token does not exist",
                response.getHeaders("x-auth") != null);
        assertTrue("user does not have id",
                userResponse.getId() != null);
        assertEquals("User email is not same",
                email, userResponse.getEmail());
    }
}
