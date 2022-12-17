package task.adsquare.tictactoe.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import task.adsquare.tictactoe.model.entity.Game;

public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("SELECT g FROM Game g WHERE g.id = :game AND (g.crossPlayerId = :player OR g.circlePlayerId = :player)")
    Optional<Game> findGameForPlayer(@Param("game") Long gameId, @Param("player") Long playerId);
}
