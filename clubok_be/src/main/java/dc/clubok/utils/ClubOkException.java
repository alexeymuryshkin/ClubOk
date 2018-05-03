package dc.clubok.utils;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;

@Getter @Setter
public class ClubOkException extends RuntimeException {
    private ClubOkResponse response;
    private int statusCode;
    private Document details;

    public ClubOkException() {
        super();
    }

    public ClubOkException(String message) {
        super(message);
    }

    public ClubOkException(ClubOkResponse response) {
        this(response, new Document());
    }
    
    public ClubOkException(ClubOkResponse response, Document details) {
        this(response, details, SC_BAD_REQUEST);
    }

    public ClubOkException(ClubOkResponse response, Document details, int statusCode) {
        setResponse(response);
        setDetails(details);
        setStatusCode(statusCode);
    }
    
    public ClubOkException(int errorCode, String errorMessage) {
        this(new ClubOkErrorResponse(errorCode, errorMessage));
    }
    
    public ClubOkException(int errorCode, String errorMessage, int statusCode) {
        this(new ClubOkErrorResponse(errorCode, errorMessage), new Document(), statusCode);
    }

    public ClubOkException(int errorCode, String errorMessage, Document details) {
        this(new ClubOkErrorResponse(errorCode, errorMessage), details);
    }

    public ClubOkException(int errorCode, String errorMessage, Document details, int statusCode) {
        this(new ClubOkErrorResponse(errorCode, errorMessage), details, statusCode);
    }
}
