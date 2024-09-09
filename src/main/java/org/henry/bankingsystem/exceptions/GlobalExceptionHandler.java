package org.henry.bankingsystem.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.henry.bankingsystem.dto.DefaultApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.henry.bankingsystem.constants.StatusCodeConstants.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DefaultApiResponse<?>> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException: {}", ex.getMessage());

        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        response.setStatusCode(GENERIC_ERROR);
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

        response.setStatusCode(GENERIC_ERROR);
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
        responseDto.setStatusCode(GENERIC_ERROR);
        responseDto.setStatusMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<DefaultApiResponse<?>> handleExpiredJWTException(ExpiredJwtException ex)
    {
        log.warn("Expired JWT Exception: {}", ex.getMessage());
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        response.setStatusCode(EXPIRED_JWT_EXCEPTION);
        response.setStatusMessage("JWT Expired: Prompt user to Login or Refresh Token");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<DefaultApiResponse<?>> handleJwtSignatureExceptions(SignatureException ex)
    {
        log.error("Signature Exception {}", ex.getMessage());
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        response.setStatusCode(SIGNATURE_EXCEPTION);
        response.setStatusMessage("JWT Expired: Prompt user to Login or Refresh Token");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    public ResponseEntity<DefaultApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex)
    {
        log.error("Resource Not Found Exception: {}", ex.getMessage());
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        response.setStatusCode(RESOURCE_NOT_FOUND_EXCEPTION);
        response.setStatusMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

}
