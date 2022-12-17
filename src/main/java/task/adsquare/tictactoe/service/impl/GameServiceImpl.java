package task.adsquare.tictactoe.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.adsquare.tictactoe.exception.InvalidGameActivityException;
import task.adsquare.tictactoe.model.GameStatus;
import task.adsquare.tictactoe.model.PositionMarker;
import task.adsquare.tictactoe.model.dto.request.CreateGameDto;
import task.adsquare.tictactoe.model.dto.request.MarkPositionDto;
import task.adsquare.tictactoe.model.dto.response.GameDto;
import task.adsquare.tictactoe.model.dto.response.GamePositionDto;
import task.adsquare.tictactoe.model.entity.Game;
import task.adsquare.tictactoe.model.entity.GamePosition;
import task.adsquare.tictactoe.model.entity.GamePositionId;
import task.adsquare.tictactoe.repository.GamePositionRepository;
import task.adsquare.tictactoe.repository.GameRepository;
import task.adsquare.tictactoe.service.GameService;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final GamePositionRepository gamePositionRepository;

    @Autowired
    public GameServiceImpl(GameRepository gameRepository, GamePositionRepository gamePositionRepository) {
        this.gameRepository = gameRepository;
        this.gamePositionRepository = gamePositionRepository;
    }

    @Transactional
    @Override
    public GameDto markPosition(MarkPositionDto moveDto, long gameId) {

        long playerId = moveDto.getPlayerId();

        Optional<Game> gameOptional = gameRepository.findGameForPlayer(gameId, playerId);
        Game game = gameOptional
                .orElseThrow(() -> new InvalidGameActivityException("WRONG_ID_PAIR", "Invalid Game and Player ID "
                        + "pairing"));

        if (!game.getStatus().equals(GameStatus.IN_PROGRESS)) {
            throw new InvalidGameActivityException("GAME_OVER", "This game is over. No moves possible");
        }

        Long lastMovePlayerID = game.getLastMovePlayerId();

        if (lastMovePlayerID != null && lastMovePlayerID.equals(playerId)) {
            throw new InvalidGameActivityException("WRONG_PLAYER_MOVE", "Not the turn of this player");
        }

        GamePositionId gamePositionId = new GamePositionId();
        gamePositionId.setPosition(moveDto.getPosition());
        gamePositionId.setGameId(gameId);

        if (gamePositionRepository.existsById(gamePositionId)) {
            throw new InvalidGameActivityException("ALREADY_MARKED_POSITION", "This position is already marked");
        }

        PositionMarker playerMarker;

        if (game.getCirclePlayerId().equals(playerId)) {
            playerMarker = PositionMarker.CIRCLE;
        } else {
            playerMarker = PositionMarker.CROSS;
        }

        GamePosition newGamePosition = new GamePosition();
        newGamePosition.setGameId(game.getId());
        newGamePosition.setPosition(moveDto.getPosition());
        newGamePosition.setMarker(playerMarker);
        newGamePosition.setOccupiedBy(playerId);

        game.setLastMovePlayerId(playerId);
        List<GamePosition> gamePositions = game.getGamePositions();
        gamePositions.add(newGamePosition);
        game.setGamePositions(gamePositions);

        if (hasPlayerWon(gamePositions, playerMarker)) {
            switch (playerMarker) {
                case CROSS:
                    game.setStatus(GameStatus.CROSS_PLAYER_VICTORY);
                    break;
                case CIRCLE:
                    game.setStatus(GameStatus.CIRCLE_PLAYER_VICTORY);
                    break;
            }
        } else if (game.getGamePositions().size() == 9) {
            game.setStatus(GameStatus.DRAW);
        }

        game = gameRepository.save(game);
        return makeGameDto(game);
    }

    @Override
    public GameDto createGame(CreateGameDto createGameDto) {

        long playerOne = createGameDto.getPlayerOneId();
        long playerTwo = createGameDto.getPlayerTwoId();

        if (playerTwo == playerOne) {
            throw new InvalidGameActivityException("DUPLICATE_PLAYER_ID", "Players 1 and 2 must have different IDs");
        }

        Game game = new Game();
        game.setCirclePlayerId(playerTwo);
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setCrossPlayerId(playerOne);
        game = gameRepository.save(game);
        return makeGameDto(game);
    }

    private boolean hasPlayerWon(List<GamePosition> gamePositions, PositionMarker playerMarker) {

        if (!(gamePositions.size() < 5)) {
            List<Integer> playerPositions = gamePositions.stream()
                                                         .filter(gp -> gp.getMarker().equals(playerMarker))
                                                         .map(GamePosition::getPosition)
                                                         .collect(Collectors.toList());

            for (Integer[] winningCombination: winningPositionsCombinations()) {
                int matchCounter = 0;
                for (Integer position: winningCombination) {
                    if (playerPositions.contains(position)) {
                        matchCounter += 1;
                    }
                }
                if (matchCounter == 3) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    // Returns a list of all the possible combinations of positions that a player could mark to win.
    private List<Integer[]> winningPositionsCombinations() {
        List<Integer[]> winningPositionsCombinations = new ArrayList<>();
        winningPositionsCombinations.add(new Integer[]{1,2,3});
        winningPositionsCombinations.add(new Integer[]{4,5,6});
        winningPositionsCombinations.add(new Integer[]{7,8,9});
        winningPositionsCombinations.add(new Integer[]{1,4,7});
        winningPositionsCombinations.add(new Integer[]{2,5,8});
        winningPositionsCombinations.add(new Integer[]{3,6,9});
        winningPositionsCombinations.add(new Integer[]{1,5,9});
        winningPositionsCombinations.add(new Integer[]{3,5,7});
        return winningPositionsCombinations;
    }

    private GameDto makeGameDto(Game game) {
        GameDto gameDto = new GameDto();
        gameDto.setGameId(game.getId());
        gameDto.setCirclePlayerId(game.getCirclePlayerId());
        gameDto.setCrossPlayerId(game.getCrossPlayerId());
        gameDto.setStatus(game.getStatus());
        List<GamePositionDto> positions = new ArrayList<>();
        if (game.getGamePositions() != null) {
            positions = game.getGamePositions().stream().map(position -> {
                GamePositionDto positionDto = new GamePositionDto();
                positionDto.setPosition(position.getPosition());
                positionDto.setPositionMarker(position.getMarker());
                return positionDto;
            }).collect(Collectors.toList());
        }

        gameDto.setOccupiedPositions(positions);
        return gameDto;
    }
}
