package ru.practicum.shareit.item.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exception.model.ErrorResponse;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("Entity not found", e);
        return new ErrorResponse(String.format("not found entity %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final ConflictException e) {
        log.error("incorrect email", e);
        return new ErrorResponse(String.format("incorrect email %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse fieldValidationExceptionHandler(BadRequestException e) {
        log.error("Unsupported status of booking", e);
        return new ErrorResponse(e.getMessage());
    }
}
