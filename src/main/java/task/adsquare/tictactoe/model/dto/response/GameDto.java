package task.adsquare.tictactoe.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import task.adsquare.tictactoe.model.GameStatus;

/**
 *  Representation of the current state of a game.
 */
@Data
public class GameDto {

    private GameStatus status;

    @JsonProperty(value = "game_id")
    private Long gameId;

    @JsonProperty(value = "cross_player_id")
    private Long crossPlayerId;

    @JsonProperty(value = "circle_player_id")
    private Long circlePlayerId;

    @JsonProperty(value = "occupied_positions")
    private List<GamePositionDto> occupiedPositions;
}
