package task.adsquare.tictactoe.model.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MarkPositionDto {

    @NotNull(message = "Player ID must be present")
    private Long playerId;

    @Max(value = 9, message = "Board position cannot exceed 9")
    @Min(value = 1, message = "Board position cannot be less than 1")
    private int position;
}
