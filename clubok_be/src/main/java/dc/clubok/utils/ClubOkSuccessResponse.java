package dc.clubok.utils;

public class ClubOkSuccessResponse extends ClubOkResponse {
    public ClubOkSuccessResponse(int code, String message) {
        super("success", code, message);
    }
}
