package dc.clubok;

import com.google.gson.reflect.TypeToken;
import dc.clubok.db.models.Post;
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

public class PostsRouteTest {
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

    @Test public void GetPosts_Authorized_SUCCESS() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/posts")
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertTrue("should return success code",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));

        List<User> usersResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), new TypeToken<List<Post>>(){}.getType());
        assertEquals("should return correct number of posts",2, usersResponse.size());
    }

    @Test public void GetPosts_Unauthorized_UNAUTHORIZED() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/posts")
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return UNAUTHORIZED",
                SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

//    TODO GET /posts/?parameters

    @Test public void PostPosts_CorrectData_SUCCESS() throws IOException, ClubOkException {
        String body = "Some text or anything else";
        String type = "meeting";

        Post post = new Post();
        post.setUserId(Seed.users.get(0).getId().toHexString());
        post.setClubId(Seed.clubs.get(1).getId().toHexString());

        post.setType(type);
        post.setBody(body);

        HttpUriRequest request = RequestBuilder.post(url + "/posts")
                .setHeader("Content-Type", JSON)
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .setEntity(new StringEntity(gson.toJson(post)))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertTrue("should return success code",
                HttpStatus.isSuccess(response.getStatusLine().getStatusCode()));
        String id = gson.fromJson(EntityUtils.toString(response.getEntity()), String.class);
        assertTrue("should return valid id", ObjectId.isValid(id));


        // Assertions in DB
        Post postDb = model.findById(id, Post.class);
        assertEquals("db should have correct number of posts", 3, model.count(Post.class));
        assertNotNull("db should create user entry", postDb);
        assertEquals("db entry should match returned id", id, postDb.getId().toHexString());
    }

    @Test public void PostPosts_Unauthorized_UNAUTHORIZED() throws IOException {
        String body = "Some text or anything else";
        String type = "meeting";

        Post post = new Post();
        post.setUserId(Seed.users.get(0).getId().toHexString());
        post.setClubId(Seed.clubs.get(1).getId().toHexString());

        post.setType(type);
        post.setBody(body);

        HttpUriRequest request = RequestBuilder.post(url + "/posts")
                .setHeader("Content-Type", JSON)
                .setEntity(new StringEntity(gson.toJson(post)))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return UNAUTHORIZED",
                SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test public void PostPosts_InvalidData_BAD_REQUEST() throws IOException {
        String body = "Some text or anything else";
        String type = "meeting";

        Post post = new Post();
        post.setUserId(Seed.users.get(1).getId().toHexString());
        post.setClubId(Seed.clubs.get(1).getId().toHexString());

        post.setType(type);
        post.setBody(body);

        HttpUriRequest request = RequestBuilder.post(url + "/posts")
                .setHeader("Content-Type", JSON)
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .setEntity(new StringEntity(gson.toJson(post)))
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return BAD_REQUEST",
                SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
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
        Post postResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), Post.class);
        assertNotNull("should return post",
                postResponse);
        assertEquals("should have correct id",
                Seed.posts.get(0).getId(), postResponse.getId());
    }

    @Test public void GetPostsId_Unauthorized_UNAUTHORIZED() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/posts/" + Seed.posts.get(1).getId().toHexString())
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return UNAUTHORIZED",
                SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test public void GetPostsId_IncorrectId_BAD_REQUEST() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/posts/dffg651s23d")
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return BAD_REQUEST",
                SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test public void GetPostsId_InvalidId_NOT_FOUND() throws IOException {
        HttpUriRequest request = RequestBuilder.get(url + "/posts/5abbb075e6aa6b26bc7058b8")
                .setHeader("x-auth", Seed.users.get(0).getTokens().get(0).getToken())
                .build();
        HttpResponse response = client.execute(request);

        // Assertions in response
        assertEquals("should return NOT_FOUND",
                SC_NOT_FOUND, response.getStatusLine().getStatusCode());
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
