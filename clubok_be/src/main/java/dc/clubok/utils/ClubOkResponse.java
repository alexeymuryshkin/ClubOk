package dc.clubok.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class ClubOkResponse {
    private String status;
    private int code;
    private String message;
}
