package dc.clubok.utils.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;

@Getter @Setter
public class ClubOkException extends Exception {
    private ErrorObject error;
    private int errorCode;
    private String errorMessage;
    private int statusCode;

    public ClubOkException() {
        super();
    }

    public ClubOkException(String message) {
        super(message);
    }

    public ClubOkException(int errorCode, String errorMessage) {
        setError(new ErrorObject(errorCode, errorMessage));
        setStatusCode(SC_BAD_REQUEST);
    }

    public ClubOkException(int errorCode, String errorMessage, int statusCode) {
        setError(new ErrorObject(errorCode, errorMessage));
        setStatusCode(statusCode);
    }

    @Getter @Setter @AllArgsConstructor
    private class ErrorObject {
        private int code;
        private String message;
    }
}
