package task.adsquare.tictactoe.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import task.adsquare.tictactoe.model.PositionMarker;

/**
 * Represents an already marked position of the game.
 */
@Data
public class GamePositionDto {

    @JsonProperty(value = "position_marker")
    private PositionMarker positionMarker;
    private int position;
}
