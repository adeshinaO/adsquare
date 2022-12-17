package task.adsquare.tictactoe.model.dto.request;

import javax.validation.constraints.Min;
import lombok.Data;

@Data
public class CreateGameDto {

    @Min(value = 1, message = "Player ID must be present")
    private Long playerOneId;

    @Min(value = 1, message = "Player ID must be present")
    private Long playerTwoId;
}
