package task.adsquare.tictactoe.model.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class GamePositionId implements Serializable {
    private int position;
    private Long gameId;
}
