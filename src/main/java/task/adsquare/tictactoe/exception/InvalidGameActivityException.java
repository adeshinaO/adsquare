package task.adsquare.tictactoe.exception;

import lombok.Data;

@Data
public class InvalidGameActivityException extends RuntimeException {
    private String code;
    public InvalidGameActivityException(String code, String message) {
        super(message);
        this.code = code;
    }
}
