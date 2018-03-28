package dc.clubok;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dc.clubok.models.Token;
import dc.clubok.models.User;
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
import java.util.List;

import static org.junit.Assert.*;

public class UsersRouteTest {
    private static Model model;
    private final Gson gson = new Gson();
    private final HttpClient client = HttpClients.createDefault();
    private final String url = "http://localhost:3000/api";

    @BeforeClass
    public static void setUp() {
        ClubOKService.main(new String[0]);
        model = new MongoModel();
    }

    @Before
    public void setDb() {
        ClubOKService.mongo.getDb().drop();
        Seed.populateUsers();
    }

    // POST /users
    @Test
    public void PostUsers_ValidData_CREATED()
            throws IOException {
        String email = "testmail@example.com";
        String password = "testPass";

        User user = new User(email, password);

        HttpUriRequest request = RequestBuilder.post(url + "/users")
                .addHeader("Content-Type", "application/json")
                .setEntity(new StringEntity(gson.toJson(user)))
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("request does not return OK status",
                201, response.getStatusLine().getStatusCode());

        // RESPONSE
        // Fetching user
        User userResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), User.class);

        // Assertions in response
        assertTrue("authorization token does not exist",
                response.getHeaders("x-auth") != null);
        assertTrue("user does not have id",
                userResponse.getId() != null);
        assertEquals("User email is not same",
                email, userResponse.getEmail());

        // Assertions in DB
        User userDB = model.findByEmail(email);
        assertEquals("number of users is incorrect",
                3, model.count(User.class));
        assertTrue("user is not created",
                userDB != null);
        assertNotEquals("password is not hashed",
                password, userDB.getPassword());
    }

    @Test
    public void PostUsers_DuplicateUser_BAD()
            throws IOException {
        HttpUriRequest request = RequestBuilder.post(url + "/users")
                .addHeader("Content-Type", "application/json")
                .setEntity(new StringEntity(gson.toJson(
                        new User(
                                Seed.users.get(0).getEmail(),
                                "SomePass"
                        ))))
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("request does not return BAD status",
                400, response.getStatusLine().getStatusCode());

        assertEquals("number of users is incorrect",
                2, model.count(User.class));
    }

    @Test
    public void PostUsers_InvalidEmail_BAD()
            throws IOException {
        String email = "invalidemail";
        String password = "testPass";

        // Requesting server
        HttpUriRequest request = RequestBuilder.post(url + "/users")
                .addHeader("Content-Type", "application/json")
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
        User userDB = model.findByEmail(email);
        assertEquals("number of users is incorrect",
                2, model.count(User.class));
        assertTrue("User exists",
                userDB == null);
    }

    @Test
    public void PostUsers_InvalidPassword_BAD()
            throws IOException {
        String email = "someMail@example.com";
        String password = "123";

        // Requesting server
        HttpUriRequest request = RequestBuilder.post(url + "/users")
                .addHeader("Content-Type", "application/json")
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
        User userDB = model.findByEmail(email);
        assertTrue("User exists",
                userDB == null);
        assertEquals("number of users is incorrect",
                2, model.count(User.class));
    }

    @Test
    public void PostUsers_NoData_BAD()
            throws IOException {
        HttpUriRequest request = RequestBuilder.post(url + "/users")
                .addHeader("Content-Type", "application/json")
                .setEntity(new StringEntity(gson.toJson(
                        new User()
                )))
                .build();

        HttpResponse response = client.execute(request);

        assertEquals("request does not return BAD status",
                400, response.getStatusLine().getStatusCode());

        assertEquals("number of users is incorrect",
                2, model.count(User.class));
    }

    // GET /users
    @Test
    public void GetUsers_OK()
            throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/users").build();
        HttpResponse response = client.execute(request);

        List<User> usersResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), new TypeToken<List<User>>(){}.getType());
        assertEquals("number of users is incorrect",
                2, usersResponse.size());
    }

    // GET /users/:id
    @Test
    public void GetUsersId_CorrectId_OK()
            throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/users/" + Seed.users.get(1).getId().toHexString())
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("request does not return OK",
                200, response.getStatusLine().getStatusCode());

        User userResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), User.class);
        assertNotNull("user is not returned",
                userResponse);
        assertEquals("user has incorrect id",
                Seed.users.get(1).getId(), userResponse.getId());
    }

    @Test
    public void GetUsersId_IncorrectId_NOTFOUND()
            throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/users/123321")
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("request does not return NOT FOUND",
                404, response.getStatusLine().getStatusCode());
    }

    // GET /users/me
    @Test
    public void GetUsersMe_Authenticated_OK()
            throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/users/me")
                .addHeader("x-auth", Seed.users.get(1).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("request does not return OK",
                200, response.getStatusLine().getStatusCode());

        User userResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), User.class);
        assertTrue("user is not returned",
                userResponse != null);
        assertEquals("user has incorrect id",
                Seed.users.get(1).getId(), userResponse.getId());
    }

    @Test
    public void GetUsersMe_IncorrectToken_NOTAUTHORIZED()
            throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/users/me")
                .addHeader("x-auth", Seed.users.get(1).getTokens().get(0).getToken() + "123")
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("request does not return NOT AUTHORIZED",
                401, response.getStatusLine().getStatusCode());

        User userResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), User.class);
        assertTrue("user is returned",
                userResponse == null);
    }

    @Test
    public void GetUsersMe_NoToken_NOTAUTHORIZED()
            throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/users/me")
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("request does not return NOT AUTHORIZED",
                401, response.getStatusLine().getStatusCode());

        User userResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), User.class);
        assertTrue("user is returned",
                userResponse == null);
    }

    // POST /users/login
    @Test
    public void PostUsersLogin_ValidEmailAndPassword_OK()
            throws IOException {
        HttpUriRequest request = RequestBuilder.post(url + "/users/login")
                .addHeader("Content-Type", "application/json")
                .setEntity(new StringEntity(gson.toJson(
                        new User(
                            Seed.users.get(1).getEmail(),
                            "userTwoPass"
                        )
                )))
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("request does not return OK",
                200, response.getStatusLine().getStatusCode());

        User userResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), User.class);

        assertTrue("header does not exist",
                response.containsHeader("x-auth"));
        assertTrue("user is not returned",
                userResponse != null);

        // Assertions in DB
        User userDB = model.findById(userResponse.getId(), User.class);
        assertTrue("user is not in DB",
                userDB != null);
        assertTrue("authentication token is not created",
                userDB.getTokens().contains(new Token("auth", response.getFirstHeader("x-auth").getValue())));

    }

    @Test
    public void PostUsersLogin_InvalidPassword_BAD()
            throws IOException {
        HttpUriRequest request = RequestBuilder.post(url + "/users/login")
                .setEntity(new StringEntity(gson.toJson(
                        new User(
                                Seed.users.get(0).getEmail(),
                                "invalidPass"
                        )
                )))
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("request does not return BAD",
                400, response.getStatusLine().getStatusCode());
        assertFalse("authentication token is returned",
                response.containsHeader("x-auth"));

        User userDb = model.findById(Seed.users.get(0).getId(), User.class);
        assertEquals("authentication token was created",
                1, userDb.getTokens().size());
    }

    @Test
    public void PostUsersLogin_NonExistingEmail_NOTFOUND()
            throws IOException {
        String email = "nonExistingMail@example.com";
        String password = "somePass";

        HttpUriRequest request = RequestBuilder.post(url + "/users/login")
                .setEntity(new StringEntity(gson.toJson(
                        new User(
                                email,
                                password
                        )
                )))
                .build();
        HttpResponse response = client.execute(request);

        assertFalse("authentication token was returned",
                response.containsHeader("x-auth"));

        assertEquals("does not return BAD",
                400, response.getStatusLine().getStatusCode());
    }

    // DELETE /users/me/token
    @Test
    public void DeleteUsersMeToken_ValidData_OK()
            throws IOException {
        HttpUriRequest request = RequestBuilder.delete(url + "/users/me/token")
                .addHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("does not return OK",
                204, response.getStatusLine().getStatusCode());
    }

}
