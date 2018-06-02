package dc.clubok;

import com.google.gson.reflect.TypeToken;
import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.Token;
import dc.clubok.db.models.User;
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
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;
import static spark.Spark.stop;

public class UserRouteTest {
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
        Seed.populateEvents();
        Seed.populatePosts();
    }

    private void assertSuccess(Document responseBody) {
        assertEquals("should return success status, but returned error (details " + responseBody.getString("details") + ")",
                "success", ((Document) responseBody.get("response")).getString("status"));
    }

    private void assertError(Document responseBody) {
        assertEquals("should return error status, but returned success",
                "error", ((Document) responseBody.get("response")).getString("status"));
    }

    private List<User> getResult(Document responseBody) {
        return gson.fromJson(gson.toJson(responseBody.get("result")), new TypeToken<List<User>>(){}.getType());
    }
    
    // GET /users
    @Test
    public void GetUsers_NoParams_SUCCESS() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/users").build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertTrue("should return success code, but " + response.getStatusLine().getStatusCode() + " returned",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertSuccess(responseBody);

        assertTrue("response should have result", responseBody.containsKey("result"));

        List<User> result = getResult(responseBody);
        assertEquals("should return correct number of users", Seed.users.size(), result.size());
    }

    @Test
    public void GetUsers_CorrectParams_SUCCESS() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/users")
                .addParameter("size", "10")
                .addParameter("page", "1")
                .addParameter("sort_by", "email.asc")
                .addParameter("email", Seed.users.get(2).getEmail())
                .build();
        HttpResponse response = client.execute(request);

//         Assertions in response
        assertTrue("should return success code, but " + response.getStatusLine().getStatusCode() + " returned",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertSuccess(responseBody);

        List<User> result = getResult(responseBody);
        assertEquals("should return correct number of users", 1, result.size());
        assertEquals("ids should match", result.get(0).getId(), Seed.users.get(2).getId());
    }

    // POST /users
    @Test
    public void PostUsers_ValidData_SUCCESS() throws IOException, ClubOkException {
        String email = "testmail@example.com";
        String password = "testPass";

        User user = new User(email, password);

        HttpUriRequest request = RequestBuilder.post(url + "/users")
                .addHeader("Content-Type", JSON)
                .setEntity(new StringEntity(gson.toJson(user)))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertTrue("should return success code, but " + response.getStatusLine().getStatusCode() + " returned",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertSuccess(responseBody);

        assertTrue("response should have user_id", responseBody.containsKey("user_id"));

        String id = responseBody.getString("user_id");
        assertTrue("should return valid id", ObjectId.isValid(id));

        // Assertions in DB
        User userDB = UserController.findByEmail(email);
        assertEquals("db should have correct number of users", Seed.users.size() + 1, model.count(User.class));
        assertNotNull("db should create user entry", userDB);
        assertEquals("db entry should match returned id", id, userDB.getId().toHexString());
        assertNotEquals("db should hash user password", password, userDB.getPassword());
    }

    @Test
    public void PostUsers_DuplicateUser_BAD_REQUEST() throws IOException, ClubOkException {
        HttpUriRequest request = RequestBuilder.post(url + "/users")
                .addHeader("Content-Type", JSON)
                .setEntity(new StringEntity(gson.toJson(
                        new User(Seed.users.get(0).getEmail(),"SomePass")
                )))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return BAD_REQUEST", SC_BAD_REQUEST, response.getStatusLine().getStatusCode());

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertError(responseBody);

        // Assertions in DB
        assertEquals("db should have correct number of users", Seed.users.size(), model.count(User.class));
    }

    @Test
    public void PostUsers_InvalidEmail_BAD_REQUEST() throws IOException, ClubOkException {
        String email = "invalidemail";
        String password = "testPass";

        // Requesting server
        HttpUriRequest request = RequestBuilder.post(url + "/users")
                .addHeader("Content-Type", JSON)
                .setEntity(new StringEntity(gson.toJson(
                        new User(email, password)
                )))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return BAD_REQUEST", SC_BAD_REQUEST, response.getStatusLine().getStatusCode());

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertError(responseBody);

        // Assertions in DB
        User userDB = UserController.findByEmail(email);
        assertEquals("db should have correct number of users",3, model.count(User.class));
        assertNull("db should not add user", userDB);
    }

    @Test
    public void PostUsers_InvalidPassword_BAD_REQUEST() throws IOException, ClubOkException {
        String email = "someMail@example.com";
        String password = "123";

        // Requesting server
        HttpUriRequest request = RequestBuilder.post(url + "/users")
                .addHeader("Content-Type", JSON)
                .setEntity(new StringEntity(gson.toJson(
                        new User(email, password)
                )))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return BAD_REQUEST", SC_BAD_REQUEST, response.getStatusLine().getStatusCode());

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertError(responseBody);

        // Assertions in DB
        User userDB = UserController.findByEmail(email);
        assertEquals("db should have correct number of users",3, model.count(User.class));
        assertNull("db should not add user", userDB);
    }

    @Test
    public void PostUsers_NoData_BAD_REQUEST() throws IOException, ClubOkException {
        HttpUriRequest request = RequestBuilder.post(url + "/users")
                .addHeader("Content-Type", JSON)
                .setEntity(new StringEntity(gson.toJson(
                        new User()
                )))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return BAD_REQUEST", SC_BAD_REQUEST, response.getStatusLine().getStatusCode());

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertError(responseBody);

        // Assertions in DB
        assertEquals("db should have correct number of users",3, model.count(User.class));
    }

    @Test
    public void PostUsers_NoEmail_BAD_REQUEST() throws IOException, ClubOkException {
        User user = new User();
        user.setPassword("somePassword");

        HttpUriRequest request = RequestBuilder.post(url + "/users")
                .addHeader("Content-Type", JSON)
                .setEntity(new StringEntity(gson.toJson(user)))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return BAD_REQUEST", SC_BAD_REQUEST, response.getStatusLine().getStatusCode());

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertError(responseBody);

        // Assertions in DB
        assertEquals("db should have correct number of users",3, model.count(User.class));
    }

    @Test
    public void PostUsers_EmptyEmail_BAD_REQUEST() throws IOException, ClubOkException {
        User user = new User();
        user.setEmail("");
        user.setPassword("somePassword");

        HttpUriRequest request = RequestBuilder.post(url + "/users")
                .addHeader("Content-Type", JSON)
                .setEntity(new StringEntity(gson.toJson(user)))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return BAD_REQUEST", SC_BAD_REQUEST, response.getStatusLine().getStatusCode());

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertError(responseBody);

        // Assertions in DB
        assertEquals("db should have correct number of users",3, model.count(User.class));
    }

    // POST /users/login
    @Test
    public void PostUsersLogin_ValidEmailAndPassword_SUCCESS() throws IOException, ClubOkException {
        HttpUriRequest request = RequestBuilder.post(url + "/users/login")
                .addHeader("Content-Type", JSON)
                .setEntity(new StringEntity(gson.toJson(
                        new User(Seed.users.get(1).getEmail(),"userOnePass")
                )))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertTrue("should return success code, but " + response.getStatusLine().getStatusCode() + " returned",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));
        assertTrue("should return authentication token", response.containsHeader("x-auth"));

        // Assertions in DB
        User userDB = model.findById(Seed.users.get(1).getId(), User.class);
        assertNotNull("db should add user", userDB);
        assertTrue("db entry should have authentication token",
                userDB.getTokens().contains(new Token("auth", response.getFirstHeader("x-auth").getValue())));
    }

    @Test
    public void PostUsersLogin_InvalidPassword_BAD_REQUEST() throws IOException, ClubOkException {
        HttpUriRequest request = RequestBuilder.post(url + "/users/login")
                .setEntity(new StringEntity(gson.toJson(
                        new User(Seed.users.get(0).getEmail(),"invalidPass")
                )))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return BAD_REQUEST", SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
        assertFalse("should not return authentication token", response.containsHeader("x-auth"));

        // Assertions in DB
        User userDb = model.findById(Seed.users.get(0).getId(), User.class);
        assertEquals("db should not create authentication token",1, userDb.getTokens().size());
    }

    @Test
    public void PostUsersLogin_NonExistingEmail_BAD_REQUEST() throws IOException {
        String email = "nonExistingMail@example.com";
        String password = "somePass";

        HttpUriRequest request = RequestBuilder.post(url + "/users/login")
                .setEntity(new StringEntity(gson.toJson(
                        new User(email, password)
                )))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return BAD_REQUEST", SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
        assertFalse("should not return authentication token", response.containsHeader("x-auth"));
    }

    // GET /users/me
    @Test
    public void GetUsersMe_Authenticated_SUCCESS() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/users/me")
                .addHeader("x-auth", Seed.users.get(1).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertTrue("should return success code, but " + response.getStatusLine().getStatusCode() + " returned",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertSuccess(responseBody);

        User user = gson.fromJson(gson.toJson(responseBody.get("result")), User.class);

        assertNotNull("should return user", user);
        assertEquals("should match user id", Seed.users.get(1).getId(), user.getId());
    }

    @Test
    public void GetUsersMe_IncorrectToken_UNAUTHORIZED() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/users/me")
                .addHeader("x-auth", Seed.users.get(1).getTokens().get(0).getToken() + "123")
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("should return UNAUTHORIZED", SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void GetUsersMe_NoToken_UNAUTHORIZED() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/users/me")
                .build();
        HttpResponse response = client.execute(request);

        assertEquals("should return UNAUTHORIZED", SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    // TODO GET /users/me/subscriptions

    // TODO DELETE /users/me/subscriptions/:id

    // TODO GET /users/me/tokens

    // TODO DELETE /users/me/tokens

    // DELETE /users/me/token
    @Test
    public void DeleteUsersMeToken_ValidData_SUCCESS() throws IOException, ClubOkException {
        HttpUriRequest request = RequestBuilder.delete(url + "/users/me/token")
                .addHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertTrue("should return success code, but " + response.getStatusLine().getStatusCode() + " returned",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        // Assertions in DB
        User userDb = model.findById(Seed.users.get(0).getId(), User.class);
        assertEquals("db should delete authentication token",
                0, userDb.getTokens().size());
    }

    // GET /users/:id
    @Test
    public void GetUsersId_CorrectId_SUCCESS() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/users/" + Seed.users.get(1).getId().toHexString())
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertTrue("should return success code, but " + response.getStatusLine().getStatusCode() + " returned",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertSuccess(responseBody);

        User user = gson.fromJson(gson.toJson(responseBody.get("result")), User.class);

        assertNotNull("should return user", user);
        assertEquals("should match user id", Seed.users.get(1).getId(), user.getId());
    }

    @Test
    public void GetUsersId_IncorrectId_NOT_FOUND() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/users/5a9d3051937f6d2ff45e7836")
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return NOT_FOUND", SC_NOT_FOUND, response.getStatusLine().getStatusCode());

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertError(responseBody);
    }

    @Test
    public void GetUsersId_InvalidId_BAD_REQUEST() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/users/123321")
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return BAD_REQUEST", SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertError(responseBody);
    }

    // TODO GET /users/:id/subscriptions

    // TODO GET /users/:id/tokens

    // TODO DELETE /users/:id
}
