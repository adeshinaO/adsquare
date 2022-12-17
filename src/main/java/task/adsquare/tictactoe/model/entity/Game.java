package task.adsquare.tictactoe.model.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import task.adsquare.tictactoe.model.GameStatus;

/**
 * Models a game of tic-tac-toe.
 */
@Data
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "cross_player_id", nullable = false, updatable = false)
    private Long crossPlayerId;

    @Column(name = "circle_player_id", nullable = false, updatable = false)
    private Long circlePlayerId;

    /**
     * ID of the last player to mark a position. This is useful in preventing players from making two consecutive moves
     */
    @Column(name = "last_move_player_id")
    private Long lastMovePlayerId;

    @Column(name = "status")
    private GameStatus status;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "game_id")
    List<GamePosition> gamePositions = new ArrayList<>();
}
