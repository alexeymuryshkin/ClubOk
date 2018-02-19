package dc.clubok.models;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Data
public class User {
    private ObjectId id;
    private String email;
    private String password;
    private String fname;
    private String lname;
    private List<ObjectId> subscriptions;
    private List<Token> tokens;

    public User() {
        setId(new ObjectId());
        tokens = new ArrayList<>();
        subscriptions = new ArrayList<>();
    }

    public User(String email, String password) {
        setEmail(email);
        setPassword(password);
    }

}
