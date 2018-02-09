package dc.clubok.models;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public final class User {
    private ObjectId id;
    private String email;
    private String password;
    private String fname;
    private String lname;
    private List<ObjectId> subscriptions;
    private List<Token> tokens;

    public User() {
        tokens = new ArrayList<>();
        subscriptions = new ArrayList<>();
    }

    public User(final String email, final String password, final String fname, final String lname) {
        setEmail(email);
        setPassword(password);
        setFname(fname);
        setLname(lname);
        tokens = new ArrayList<>();
        subscriptions = new ArrayList<>();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(final String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(final String lname) {
        this.lname = lname;
    }

    public List<ObjectId> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(final List<ObjectId> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

}
