package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        return new ApiError(List.of(), e.getMessage(), "The required object was not found.",
                HttpStatus.NOT_FOUND, LocalDateTime.now());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(ConflictException e) {
        return new ApiError(List.of(), e.getMessage(), "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT, LocalDateTime.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception e) {
        return new ApiError(List.of(), e.getMessage(), "Internal server error.",
                HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now());
    }
}