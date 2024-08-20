package org.henry.onlinebankingsystemp.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.henry.onlinebankingsystemp.dto.DefaultApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DefaultApiResponse<?>> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException: {}", ex.getMessage());

        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setStatusMessage(String.format("Unexpected Error Occurred: (%s)", ex.getMessage()));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultApiResponse<Map<String, String>>> handleInvalidArgument(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put( error.getField() , error.getDefaultMessage());
        });

        DefaultApiResponse<Map<String, String>> response = new DefaultApiResponse<>();
        log.error("Validation Failed: ({})", e.getMessage());

        response.setStatusCode(400);
        response.setStatusMessage("Validation Failed");
        response.setData(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<DefaultApiResponse<?>> handleJsonParseException(HttpMessageConversionException ex)
    {
        log.error("JsonParseException: {}", ex.getMessage());

        DefaultApiResponse<?> responseDto = new DefaultApiResponse<>();
        responseDto.setStatusCode(HttpStatus.BAD_REQUEST.value());
        responseDto.setStatusMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<DefaultApiResponse<?>> handleExpiredJWTException(ExpiredJwtException ex)
    {
        log.warn("Expired JWT Exception: {}", ex.getMessage());
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        response.setStatusMessage("JWT Expired: Prompt user to Login or Refresh Token");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<DefaultApiResponse<?>> handleJwtSignatureExceptions(SignatureException ex)
    {
        log.error("Signature Exception {}", ex.getMessage());
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        response.setStatusMessage("JWT Expired: Prompt user to Login or Refresh Token");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

}
