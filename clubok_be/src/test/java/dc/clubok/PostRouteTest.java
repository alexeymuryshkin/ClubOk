package dc.clubok;

import com.google.gson.reflect.TypeToken;
import dc.clubok.db.controllers.PostController;
import dc.clubok.db.models.Post;
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

public class PostRouteTest {
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

    private List<Post> getResult(Document responseBody) {
        return gson.fromJson(gson.toJson(responseBody.get("result")), new TypeToken<List<Post>>(){}.getType());
    }

    @Test public void GetPosts_Authorized_SUCCESS() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/posts")
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertTrue("should return success code, but " + response.getStatusLine().getStatusCode() + " returned",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertSuccess(responseBody);

        assertTrue("response should have result", responseBody.containsKey("result"));

        List<Post> result = getResult(responseBody);
        assertEquals("should return correct number of posts",Seed.posts.size(), result.size());
    }

    @Test public void GetPosts_Unauthorized_UNAUTHORIZED() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/posts")
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return UNAUTHORIZED", SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

//    TODO GET /posts/?parameters

    @Test public void PostPosts_CorrectData_SUCCESS() throws IOException, ClubOkException {
        String body = "Some text or anything else";
        String type = "regular";

        Post post = new Post(Seed.clubs.get(1), Seed.users.get(0), type, body);
        Document requestBody = Document.parse(gson.toJson(post));
        requestBody.remove("club");
        requestBody.append("club_id", post.getClub().getId());

        HttpUriRequest request = RequestBuilder.post(url + "/posts")
                .setHeader("Content-Type", JSON)
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .setEntity(new StringEntity(requestBody.toJson()))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertTrue("should return success code, but " + response.getStatusLine().getStatusCode() + " returned",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertSuccess(responseBody);

        assertTrue("response should have post_id", responseBody.containsKey("post_id"));

        String id = responseBody.getString("post_id");
        assertTrue("should return valid id", ObjectId.isValid(id));


        // Assertions in DB
        Post postDB = PostController.getPostById(id);
        assertEquals("db should have correct number of posts", Seed.posts.size() + 1, model.count(Post.class));
        assertNotNull("db should create user entry", postDB);
        assertEquals("db entry should match returned id", id, postDB.getId().toHexString());
    }

    @Test public void PostPosts_Unauthorized_UNAUTHORIZED() throws IOException {
        String body = "Some text or anything else";
        String type = "meeting";

        Post post = new Post(Seed.clubs.get(1), Seed.users.get(0), type, body);

        HttpUriRequest request = RequestBuilder.post(url + "/posts")
                .setHeader("Content-Type", JSON)
                .setEntity(new StringEntity(gson.toJson(post)))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return UNAUTHORIZED", SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test public void PostPosts_InvalidData_BAD_REQUEST() throws IOException {
        String body = "";
        String type = "meeting";

        Post post = new Post(Seed.clubs.get(1), Seed.users.get(1), type, body);

        HttpUriRequest request = RequestBuilder.post(url + "/posts")
                .setHeader("Content-Type", JSON)
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .setEntity(new StringEntity(gson.toJson(post)))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return BAD_REQUEST", SC_BAD_REQUEST, response.getStatusLine().getStatusCode());

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertError(responseBody);
    }

//    TODO GET /posts/:id
    @Test public void GetPostsId_CorrectId_SUCCESS() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/posts/" + Seed.posts.get(0).getId().toHexString())
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertTrue("should return success code, but returned " + response.getStatusLine().getStatusCode(),
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertSuccess(responseBody);

        Post post = gson.fromJson(gson.toJson(responseBody.get("result")), Post.class);
        assertNotNull("should return post", post);
        assertEquals("should match post id", Seed.posts.get(0).getId(), post.getId());
    }

    @Test public void GetPostsId_Unauthorized_UNAUTHORIZED() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/posts/" + Seed.posts.get(1).getId().toHexString())
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return UNAUTHORIZED", SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test public void GetPostsId_IncorrectId_BAD_REQUEST() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/posts/dffg651s23d")
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return BAD_REQUEST", SC_BAD_REQUEST, response.getStatusLine().getStatusCode());

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertError(responseBody);
    }

    @Test public void GetPostsId_InvalidId_NOT_FOUND() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/posts/5abbb075e6aa6b26bc7058b8")
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return NOT_FOUND", SC_NOT_FOUND, response.getStatusLine().getStatusCode());

        Document responseBody = Document.parse(EntityUtils.toString(response.getEntity()));
        assertError(responseBody);
    }

//    TODO DELETE /posts/:id

//    TODO PATCH /posts/:id

//    TODO GET /posts/:id/comments

//    TODO POST /posts/:id/comments

//    TODO DELETE /posts/:id/comments/:id

//    TODO PATCH /posts/:id/comments/:id

//    TODO GET /posts/:id/likes

//    TODO POST /posts/:id/likes

//    TODO DELETE /posts/:id/moderators/:id
}
