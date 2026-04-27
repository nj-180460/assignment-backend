package com.gler.assignment.exception;

import com.gler.assignment.dto.response.ErrorGenericResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorGenericResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(
                errorBuilder(
                        "400",
                        "Bad Request",
                        errorMessage
                )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorGenericResponse> handleTypeMismatch(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(
                        errorBuilder(
                                "400",
                                "Bad Request: \"Invalid input format: Ensure data types (boolean, numbers, etc.) are correct.\"",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorGenericResponse> handleCustomBadRequest(BadRequestException ex) {
        return ResponseEntity.badRequest().body(
                errorBuilder(
                        "400",
                        "Bad Request",
                        ex.getMessage()
                )
        );
    }

    @ExceptionHandler(WeatherServiceException.class)
    public ResponseEntity<ErrorGenericResponse> handleExternalServiceException(WeatherServiceException ex) {
        // We return 502 (Bad Gateway) because the "upstream" server failed
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                errorBuilder(
                        ex.getStatusCode() + "",
                        "Weather client api call error",
                        ex.getMessage()
                )
        );
    }

    // Handles THE 500 INTERNAL SERVER ERROR (Catch-all)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorGenericResponse> handleAllUncaughtException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                errorBuilder(
                        "500",
                        "Internal Server Error",
                        "An unexpected error occurred. Please try again later." +ex.getMessage()
                )
        );
    }


    private ErrorGenericResponse errorBuilder(String status, String error, String message) {
        return new ErrorGenericResponse(
                LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)),
                status,
                error,
                message,
                "/api/v1/forcast"
        );
    }
}
