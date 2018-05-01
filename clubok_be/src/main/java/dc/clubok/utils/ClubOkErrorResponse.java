package dc.clubok.utils;

public class ClubOkErrorResponse extends ClubOkResponse {
    public ClubOkErrorResponse(int code, String message) {
        super("error", code, message);
    }
}
