package ru.practicum.exceptions;

public class IllegalOperationException extends RuntimeException {
    public IllegalOperationException(final String message) {
        super(message);
    }
}
