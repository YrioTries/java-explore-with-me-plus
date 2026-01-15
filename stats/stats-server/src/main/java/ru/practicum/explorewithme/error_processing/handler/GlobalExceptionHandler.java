package ru.practicum.explorewithme.error_processing.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.error_processing.exceptions.ValidationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestsException(Exception e) {
        log.info("Получен статус 404 NOT_FOUND {}", e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND.toString())
                .message(e.getMessage())
                .reason("Bad Request error. Input data is not valid.")
                .build();
    }
}
