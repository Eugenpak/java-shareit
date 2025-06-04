package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.Objects;

@ControllerAdvice("ru.practicum.shareit")
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public  ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        String message;
        try {
            String field = Objects.requireNonNull(e.getBindingResult().getFieldError()).getField();
            message = String.format("Поле %s %s", field, errorMessage);

            log.error(message);
        } catch (NullPointerException ex) {
            log.error(errorMessage);
            message = errorMessage;
        }
        return new ResponseEntity<>(new ErrorResponse(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleBadParams(final MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(String.format("G. Unknown %s: %s", e.getName(), e.getValue())),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleThrowable(final Exception e) {
        log.error(e.getLocalizedMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ErrorResponse {
        String error;
    }
}
