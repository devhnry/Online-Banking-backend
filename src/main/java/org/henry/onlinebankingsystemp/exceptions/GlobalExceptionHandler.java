package org.henry.onlinebankingsystemp.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.henry.onlinebankingsystemp.dto.DefaultApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DefaultApiResponse<?>> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException: {}", ex.getMessage());

        DefaultApiResponse<?> responseDto = new DefaultApiResponse<>();

        responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseDto.setStatusMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DefaultApiResponse<?>> handleException(IllegalArgumentException e) {
        DefaultApiResponse<?> response = new DefaultApiResponse<>();

        response.setStatusCode(400);
        response.setStatusMessage(String.format("Validation Failed: (%s)", e.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
