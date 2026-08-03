package com.app.my_project;

import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * Turns the exceptions the controllers throw into the HTTP status the caller
 * expects. Without this every failure - a duplicate username, a missing record,
 * a foreign key that still has children - surfaces as 500 Internal Server Error,
 * which tells a client "the server is broken" when the request was simply wrong.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    public record ApiError(int status, String error, String message) {
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ApiError(status.value(), status.getReasonPhrase(), message));
    }

    /**
     * The controllers signal "record does not exist" by putting `not found` in the
     * message rather than by throwing a dedicated type. Matching on the message is
     * a stopgap: the proper fix is a NotFoundException thrown from the repositories,
     * which would let this handler dispatch on type like the others below.
     */
    private boolean isNotFound(String message) {
        return message != null && message.toLowerCase().contains("not found");
    }

    /**
     * ResponseStatusException already carries the status the caller should see
     * (signin uses it for 401). It extends RuntimeException, so without this
     * handler the catch-all below would swallow it and answer 500 instead.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException e) {
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
        return build(status, e.getReason());
    }

    // Business rule refused the request - the cart is empty, the product has no price
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException e) {
        return build(HttpStatus.CONFLICT, e.getMessage());
    }

    // The database refused it: duplicate username, or a row still referenced by others
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException e) {
        return build(HttpStatus.CONFLICT,
                "This record conflicts with existing data - it may be a duplicate, or still referenced elsewhere");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNoSuchElement(NoSuchElementException e) {
        return build(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // Bad input from the caller, or a lookup that came up empty
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException e) {
        HttpStatus status = isNotFound(e.getMessage()) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return build(status, e.getMessage());
    }

    // Anything left over. A "not found" message still maps to 404; everything else
    // is a genuine server fault, and the detail stays in the log rather than the body.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntime(RuntimeException e) {
        if (isNotFound(e.getMessage())) {
            return build(HttpStatus.NOT_FOUND, e.getMessage());
        }
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error");
    }
}
