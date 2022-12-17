package task.adsquare.tictactoe.service;

import org.springframework.transaction.annotation.Transactional;
import task.adsquare.tictactoe.exception.InvalidGameActivityException;
import task.adsquare.tictactoe.model.dto.request.CreateGameDto;
import task.adsquare.tictactoe.model.dto.response.GameDto;
import task.adsquare.tictactoe.model.dto.request.MarkPositionDto;

public interface GameService {

    /**
     * Marks a position for a player in an active game. Throws an {@link InvalidGameActivityException} if:
     * - The game is over. No new moves possible.
     * - It's not the turn of the player to make a move.
     * - The position is already marked.
     * - Player is not a participant in the game with the given game ID.
     * @return A {@link GameDto}
     */
    @Transactional
    GameDto markPosition(MarkPositionDto moveDto, long gameId);

    /**
     * Creates a new game for two players.
     */
    GameDto createGame(CreateGameDto createGameDto);
}
