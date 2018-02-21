package dc.clubok;

import com.google.gson.Gson;
import dc.clubok.data.MongoUserDB;
import dc.clubok.models.User;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class UsersRouteTest {
    private final Gson gson = new Gson();
    private final HttpClient client = HttpClients.createDefault();
    private final String url = "http://localhost:3000";

    private final static List<User> users = Arrays.asList(
            new User("userOneEmail@example.com", "userOnePass"),
            new User("userTwoEmail@example.com", "userTwoPass")
    );

    @BeforeClass
    public static void setUp() {
        App.main(new String[0]);
    }

    @Before
    public void setDb() {
        App.mongo.getDb().drop();
        for (User user: users) {
            try {
                MongoUserDB.save(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test public void PostUsers_ValidData_OK()
            throws IOException {
        String email = "testmail@example.com";
        String password = "testPass";

        HttpUriRequest request = RequestBuilder.post(url + "/users")
                .addHeader("content-type", "application/json")
                .setEntity(new StringEntity(gson.toJson(
                        new User(
                                email,
                                password
                        )
                )))
                .build();
        HttpResponse response = client.execute(request);

        // RESPONSE
        // Fetching user
        User userResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), User.class);

        // Assertions in response
        assertEquals("request does not return OK status",
                200, response.getStatusLine().getStatusCode());
        assertTrue("authorization token does not exist",
                response.getHeaders("x-auth") != null);
        assertTrue("user does not have id",
                userResponse.getId() != null);
        assertEquals("User email is not same",
                email, userResponse.getEmail());

        // Assertions in DB
        User userDB = MongoUserDB.findByEmail(email);
        assertTrue("User was not created",
                userDB != null);
        assertNotEquals("Password was not hashed",
                password, userDB.getPassword());
    }

    @Test public void PostUsers_DuplicateUser_BAD()
            throws IOException {
        HttpUriRequest request = RequestBuilder.post(url + "/users")
                .addHeader("content-type", "application/json")
                .setEntity(new StringEntity(gson.toJson(
                        new User(
                                users.get(0).getEmail(),
                                users.get(0).getPassword()
                        ))))
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("request does not return BAD status",
                400, response.getStatusLine().getStatusCode());
    }

    @Test public void PostUsers_InvalidEmail_BAD()
            throws IOException {
        String email = "invalidemail";
        String password = "testPass";

        // Requesting server
        HttpUriRequest request = RequestBuilder.post(url + "/users")
                .addHeader("content-type", "application/json")
                .setEntity(new StringEntity(gson.toJson(
                        new User(
                            email,
                            password
                        )
                )))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("request does not return BAD status",
                400, response.getStatusLine().getStatusCode());

        // Assertions in DB
        User userDB = MongoUserDB.findByEmail(email);
        assertTrue("User exists",
                userDB == null);
    }

    @Test public void PostUsers_InvalidPassword_BAD()
            throws IOException {
        String email = "someMail@example.com";
        String password = "123";

        // Requesting server
        HttpUriRequest request = RequestBuilder.post(url + "/users")
                .addHeader("content-type", "application/json")
                .setEntity(new StringEntity(gson.toJson(
                        new User(
                                email,
                                password
                        )
                )))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("request does not return BAD status",
                400, response.getStatusLine().getStatusCode());

        // Assertions in DB
        User userDB = MongoUserDB.findByEmail(email);
        assertTrue("User exists",
                userDB == null);
    }

}
