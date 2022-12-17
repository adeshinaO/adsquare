package task.adsquare.tictactoe.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import task.adsquare.tictactoe.exception.InvalidGameActivityException;
import task.adsquare.tictactoe.model.dto.response.ApiErrorDto;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(InvalidGameActivityException.class)
    public ResponseEntity<ApiErrorDto> handleInvalidGameMove(InvalidGameActivityException exception) {
        ApiErrorDto apiErrorDto = new ApiErrorDto();
        apiErrorDto.setCode(exception.getCode());
        apiErrorDto.setDescription(exception.getMessage());
        return new ResponseEntity<>(apiErrorDto, HttpStatus.BAD_REQUEST);
    }
}
