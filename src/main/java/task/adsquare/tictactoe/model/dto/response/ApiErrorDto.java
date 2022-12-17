package task.adsquare.tictactoe.model.dto.response;

import lombok.Data;

@Data
public class ApiErrorDto {
    private String code;
    private String description;
}
