package task.adsquare.tictactoe.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.Data;
import task.adsquare.tictactoe.model.PositionMarker;

/**
 * Models a position on a 3x3 grid that can be marked either by a circle or cross(x). A position is considered marked
 * if an entry for it exists on the underlying table.
 */
@Data
@Entity
@Table(name = "game_positions")
@IdClass(GamePositionId.class)
public class GamePosition {

    @Id
    @Column(name = "game_id", nullable = false, updatable = false)
    private Long gameId;

    @Id
    @Column(name = "position")
    private int position;

    @Column(name = "occupied_by", nullable = false, updatable = false)
    private Long occupiedBy;

    @Column(name = "position_marker", nullable = false, updatable = false)
    private PositionMarker marker;
}
