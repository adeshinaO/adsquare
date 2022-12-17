package task.adsquare.tictactoe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import task.adsquare.tictactoe.model.GameStatus;
import task.adsquare.tictactoe.model.PositionMarker;
import task.adsquare.tictactoe.model.dto.request.CreateGameDto;
import task.adsquare.tictactoe.model.dto.request.MarkPositionDto;
import task.adsquare.tictactoe.model.entity.Game;
import task.adsquare.tictactoe.model.entity.GamePosition;
import task.adsquare.tictactoe.repository.GamePositionRepository;
import task.adsquare.tictactoe.repository.GameRepository;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GameControllerTest {

    private static final long CIRCLE_PLAYER_ID = 1L;
    private static final long CROSS_PLAYER_ID = 2L;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePositionRepository gamePositionRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Create A Game Successfully")
    public void createGameReturnsIsSuccessful() throws Exception {
        CreateGameDto createGameDto = new CreateGameDto();
        createGameDto.setPlayerOneId(CROSS_PLAYER_ID);
        createGameDto.setPlayerTwoId(CIRCLE_PLAYER_ID);

        String jsonBody = objectMapper.writeValueAsString(createGameDto);

        // Create Game - Check that ID exists in response and record is in DB.
        this.mockMvc.perform(post("/api/v1/game")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.game_id").exists());

        Assertions.assertFalse(gameRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Create Game With Duplicate Player IDs - Should Return Error Description")
    public void createGameWithMissingPlayerId() throws Exception {

        // Create Game - Use same ID for both players
        CreateGameDto createGameDto = new CreateGameDto();
        createGameDto.setPlayerOneId(CIRCLE_PLAYER_ID);
        createGameDto.setPlayerTwoId(CIRCLE_PLAYER_ID);
        String jsonBody = objectMapper.writeValueAsString(createGameDto);

        // Should fail with appropriate error code
        this.mockMvc.perform(post("/api/v1/game")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.code").value("DUPLICATE_PLAYER_ID"));

        Assertions.assertTrue(gameRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Mark Empty Game Position - Expect Success")
    public void markEmptyPositionForPlayer() throws Exception {
        int position = 6;

        // Create Game For Two Different Players
        Game game = new Game();
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setCirclePlayerId(CIRCLE_PLAYER_ID);
        game.setCrossPlayerId(CROSS_PLAYER_ID);

        long gameId = gameRepository.save(game).getId();

        // Mark position 6 with a circle
        MarkPositionDto markPositionDto = new MarkPositionDto();
        markPositionDto.setPosition(position);
        markPositionDto.setPlayerId(CIRCLE_PLAYER_ID);
        String jsonBody = objectMapper.writeValueAsString(markPositionDto);

        // Mark Position 6 with a circle
        String url = String.format("/api/v1/game/%s/mark", gameId);
        this.mockMvc.perform(post(url)
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.game_id").exists())
                    .andExpect(jsonPath("$.occupied_positions[0].position_marker").value(PositionMarker.CIRCLE.toString()))
                    .andExpect(jsonPath("$.occupied_positions[0].position").value(position));
    }

    @Test
    @DisplayName("Mark Occupied Game Position - Should Fail")
    public void markOccupiedPosition() throws Exception {

        // Create new game for two players
        Game game = new Game();
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setCirclePlayerId(CIRCLE_PLAYER_ID);
        game.setCrossPlayerId(CROSS_PLAYER_ID);
        long gameId = gameRepository.save(game).getId();

        int position = 6;

        // Marks position 6 for cross player.
        GamePosition gamePosition = new GamePosition();
        gamePosition.setGameId(gameId);
        gamePosition.setPosition(position);
        gamePosition.setOccupiedBy(CROSS_PLAYER_ID);
        gamePosition.setMarker(PositionMarker.CROSS);
        gamePositionRepository.save(gamePosition);

        // Attempt to mark position 6 for player one (circle).
        MarkPositionDto markPositionDto = new MarkPositionDto();
        markPositionDto.setPosition(position);
        markPositionDto.setPlayerId(CIRCLE_PLAYER_ID);
        String jsonBody = objectMapper.writeValueAsString(markPositionDto);

        String url = String.format("/api/v1/game/%s/mark", gameId);

        // Should return 4XX and the appropriate error representation.
        this.mockMvc.perform(post(url)
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.code").value("ALREADY_MARKED_POSITION"));
    }

    @Test
    @DisplayName("Mark Empty Position That Wins Game - Expect Game Status Change")
    public void markPositionToWin() throws Exception {

        // Create Game For Two Players
        Game game = new Game();
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setCirclePlayerId(CIRCLE_PLAYER_ID);
        game.setCrossPlayerId(CROSS_PLAYER_ID);

        long gameId = gameRepository.save(game).getId();

        // Mark positions 1 and 3 for cross player.
        GamePosition gamePositionOne = new GamePosition();
        gamePositionOne.setGameId(gameId);
        gamePositionOne.setPosition(1);
        gamePositionOne.setOccupiedBy(CROSS_PLAYER_ID);
        gamePositionOne.setMarker(PositionMarker.CROSS);
        gamePositionRepository.save(gamePositionOne);

        GamePosition gamePositionSix = new GamePosition();
        gamePositionSix.setGameId(gameId);
        gamePositionSix.setPosition(3);
        gamePositionSix.setOccupiedBy(CROSS_PLAYER_ID);
        gamePositionSix.setMarker(PositionMarker.CROSS);
        gamePositionRepository.save(gamePositionSix);

        // Mark positions 4 and 5 circle player

        GamePosition gamePositionThree = new GamePosition();
        gamePositionThree.setGameId(gameId);
        gamePositionThree.setPosition(4);
        gamePositionThree.setOccupiedBy(CIRCLE_PLAYER_ID);
        gamePositionThree.setMarker(PositionMarker.CIRCLE);
        gamePositionRepository.save(gamePositionThree);

        GamePosition gamePositionFive = new GamePosition();
        gamePositionFive.setGameId(gameId);
        gamePositionFive.setPosition(5);
        gamePositionFive.setOccupiedBy(CIRCLE_PLAYER_ID);
        gamePositionFive.setMarker(PositionMarker.CIRCLE);
        gamePositionRepository.save(gamePositionFive);

        // Attempt to mark position 6 for player one (circle).
        MarkPositionDto markPositionDto = new MarkPositionDto();
        markPositionDto.setPosition(6);
        markPositionDto.setPlayerId(CIRCLE_PLAYER_ID);
        String jsonBody = objectMapper.writeValueAsString(markPositionDto);

        String url = String.format("/api/v1/game/%s/mark", gameId);

        // Should return 200 and a new game status - Circle Player Victory
        this.mockMvc.perform(post(url)
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.status").value(GameStatus.CIRCLE_PLAYER_VICTORY.name()));
    }

    @Test
    @DisplayName("Mark Empty Position That Ends Game In A Draw")
    public void markPositionToDraw() throws Exception {

        // Create Game For Two Players
        Game game = new Game();
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setCirclePlayerId(CIRCLE_PLAYER_ID);
        game.setCrossPlayerId(CROSS_PLAYER_ID);

        long gameId = gameRepository.save(game).getId();

        // Mark positions 1, 3, 4, 8, 9 for cross player.
        GamePosition gamePositionOne = new GamePosition();
        gamePositionOne.setGameId(gameId);
        gamePositionOne.setPosition(1);
        gamePositionOne.setOccupiedBy(CROSS_PLAYER_ID);
        gamePositionOne.setMarker(PositionMarker.CROSS);
        gamePositionRepository.save(gamePositionOne);

        GamePosition gamePositionThree = new GamePosition();
        gamePositionThree.setGameId(gameId);
        gamePositionThree.setPosition(3);
        gamePositionThree.setOccupiedBy(CROSS_PLAYER_ID);
        gamePositionThree.setMarker(PositionMarker.CROSS);
        gamePositionRepository.save(gamePositionThree);

        GamePosition gamePositionFour = new GamePosition();
        gamePositionFour.setGameId(gameId);
        gamePositionFour.setPosition(4);
        gamePositionFour.setOccupiedBy(CROSS_PLAYER_ID);
        gamePositionFour.setMarker(PositionMarker.CROSS);
        gamePositionRepository.save(gamePositionFour);

        GamePosition gamePositionEight = new GamePosition();
        gamePositionEight.setGameId(gameId);
        gamePositionEight.setPosition(8);
        gamePositionEight.setOccupiedBy(CROSS_PLAYER_ID);
        gamePositionEight.setMarker(PositionMarker.CROSS);
        gamePositionRepository.save(gamePositionEight);

        GamePosition gamePositionNine = new GamePosition();
        gamePositionNine.setGameId(gameId);
        gamePositionNine.setPosition(9);
        gamePositionNine.setOccupiedBy(CROSS_PLAYER_ID);
        gamePositionNine.setMarker(PositionMarker.CROSS);
        gamePositionRepository.save(gamePositionNine);

        // Mark positions 2, 5, 6, for circle player

        GamePosition gamePositionTwo = new GamePosition();
        gamePositionTwo.setGameId(gameId);
        gamePositionTwo.setPosition(2);
        gamePositionTwo.setOccupiedBy(CIRCLE_PLAYER_ID);
        gamePositionTwo.setMarker(PositionMarker.CIRCLE);
        gamePositionRepository.save(gamePositionTwo);

        GamePosition gamePositionFive = new GamePosition();
        gamePositionFive.setGameId(gameId);
        gamePositionFive.setPosition(5);
        gamePositionFive.setOccupiedBy(CIRCLE_PLAYER_ID);
        gamePositionFive.setMarker(PositionMarker.CIRCLE);
        gamePositionRepository.save(gamePositionFive);

        GamePosition gamePositionSix = new GamePosition();
        gamePositionSix.setGameId(gameId);
        gamePositionSix.setPosition(6);
        gamePositionSix.setOccupiedBy(CIRCLE_PLAYER_ID);
        gamePositionSix.setMarker(PositionMarker.CIRCLE);
        gamePositionRepository.save(gamePositionSix);

        // Attempt to mark position 7 for player one (circle).
        MarkPositionDto markPositionDto = new MarkPositionDto();
        markPositionDto.setPosition(7);
        markPositionDto.setPlayerId(CIRCLE_PLAYER_ID);
        String jsonBody = objectMapper.writeValueAsString(markPositionDto);

        String url = String.format("/api/v1/game/%s/mark", gameId);

        // Should return 200 and a new game status - Draw.
        this.mockMvc.perform(post(url)
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.status").value(GameStatus.DRAW.name()));
    }
}
