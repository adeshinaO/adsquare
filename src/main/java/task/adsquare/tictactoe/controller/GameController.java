package task.adsquare.tictactoe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task.adsquare.tictactoe.model.dto.request.CreateGameDto;
import task.adsquare.tictactoe.model.dto.request.MarkPositionDto;
import task.adsquare.tictactoe.model.dto.response.GameDto;
import task.adsquare.tictactoe.service.GameService;

@RestController
@RequestMapping("/api/v1/game")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<GameDto> createGame(@Validated @RequestBody CreateGameDto createGameDto) {
        GameDto dto = gameService.createGame(createGameDto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PostMapping("/{gameId}/mark")
    public ResponseEntity<GameDto> markPosition(
            @Validated @RequestBody MarkPositionDto markPositionDto,
            @PathVariable("gameId") Long gameId) {
        GameDto dto = gameService.markPosition(markPositionDto, gameId);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }
}
