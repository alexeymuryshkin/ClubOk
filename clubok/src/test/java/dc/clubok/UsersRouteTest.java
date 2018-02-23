package dc.clubok;

import com.google.gson.Gson;
import dc.clubok.entities.Entity;
import dc.clubok.entities.Token;
import dc.clubok.entities.User;
import dc.clubok.entities.models.UserModel;
import dc.clubok.mongomodel.MongoUserModel;
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

public class UsersRouteTest {
    private static Validator validator;
    private static UserModel userModel;
    private final Gson gson = new Gson();
    private final HttpClient client = HttpClients.createDefault();
    private final String url = "http://localhost:3000";

    @BeforeClass
    public static void setUp() {
        ClubOKService.main(new String[0]);
        validator = ClubOKService.validator;
        userModel = new MongoUserModel();
    }

    @Before
    public void setDb() {
        ClubOKService.mongo.getDb().drop();
        Seed.populateUsers();
    }

    // POST /users
    @Test
    public void PostUsers_ValidData_OK()
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
                200, response.getStatusLine().getStatusCode());

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
        User userDB = userModel.findByEmail(email);
        assertEquals("number of users is incorrect",
                3, userModel.count());
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
                                Seed.users.get(0).getPassword()
                        ))))
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("request does not return BAD status",
                400, response.getStatusLine().getStatusCode());

        assertEquals("number of users is incorrect",
                2, userModel.count());
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
        User userDB = userModel.findByEmail(email);
        assertEquals("number of users is incorrect",
                2, userModel.count());
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
        User userDB = userModel.findByEmail(email);
        assertTrue("User exists",
                userDB == null);
        assertEquals("number of users is incorrect",
                2, userModel.count());
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
                2, userModel.count());
    }

    // GET /users/me
    @Test
    public void GetUsersMe_Authenticated_OK()
            throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/users/me")
                .addHeader("x-auth", Seed.users.get(1).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("request does not retun OK",
                200, response.getStatusLine().getStatusCode());

        User userResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), User.class);
        assertTrue("user is not returned",
                userResponse != null);
        assertEquals("user has correct id",
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
        User userDB = userModel.findById(userResponse.getId());
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

        User userDb = userModel.findById(Seed.users.get(0).getId());
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
                200, response.getStatusLine().getStatusCode());

        User userResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), User.class);
        assertTrue("user was not returned",
                userResponse != null);

        assertTrue("authentication token was not deleted",
                userResponse.getTokens().size() == 0);
    }

}
