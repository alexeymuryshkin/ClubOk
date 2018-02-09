package dc.clubok.models;

public class Token {
    private String access;
    private String token;

    public Token() {
    }

    public Token(String access, String token) {
        this.access = access;
        this.token = token;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
