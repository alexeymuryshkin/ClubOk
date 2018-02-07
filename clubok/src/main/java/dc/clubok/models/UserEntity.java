package dc.clubok.models;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class UserEntity extends BaseEntity {

    private String email;
    private String password;
    private String fname;
    private String lname;
    private List<ClubEntity> subscriptions;
    private List<String> tokens;

    public UserEntity() {

    }

    public UserEntity(String email, String password, String fname, String lname) {
        this.email = email;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
        this.subscriptions = new ArrayList<>();
        this.tokens = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public List<ClubEntity> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<ClubEntity> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public static UserEntity fromDocument(Document document) {
//        TODO Finish implementation of fromDocument method
        UserEntity user = new UserEntity();
        user.setEmail(document.getString("email"));
        user.setPassword(document.getString("password"));
        user.setFname(document.getString("fname"));
        user.setLname(document.getString("lname"));

//        TODO Add subscriptions and tokens parser

        return user;
    }

    public static Document toDocument(UserEntity userEntity) {
//        TODO Add implementation of toDocument method

        return null;
    }
}
