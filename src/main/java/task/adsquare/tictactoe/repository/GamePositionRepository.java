package task.adsquare.tictactoe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import task.adsquare.tictactoe.model.entity.GamePosition;
import task.adsquare.tictactoe.model.entity.GamePositionId;

public interface GamePositionRepository extends JpaRepository<GamePosition, GamePositionId> {
}
