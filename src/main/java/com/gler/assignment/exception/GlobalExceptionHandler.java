package com.gler.assignment.exception;

import com.gler.assignment.dto.response.ErrorGenericResponse;
import com.gler.assignment.util.DateUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorGenericResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(
                apiErrorBuilder(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request: Missing Parameter/s",
                        errorMessage,
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorGenericResponse> handleTypeMismatch(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String detail = "Invalid input format: Ensure data types are correct.";

        if (ex.getCause() instanceof com.fasterxml.jackson.databind.exc.MismatchedInputException mismatchedEx) {
            if (!mismatchedEx.getPath().isEmpty()) {
                String fieldName = mismatchedEx.getPath().get(0).getFieldName();
                detail = String.format("Invalid value for parameter: '%s'. Expected a boolean (true/false).", fieldName);
            }
        }
        return ResponseEntity.badRequest()
                .body(
                        apiErrorBuilder(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request: Invalid input format type",
                                detail,
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorGenericResponse> handleCustomBadRequest(BadRequestException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(
                apiErrorBuilder(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    // Handles THE 5xx INTERNAL SERVER ERROR (Catch-all)
    @ExceptionHandler(WeatherServiceException.class)
    public ResponseEntity<ErrorGenericResponse> handleExternalServiceException(WeatherServiceException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                apiErrorBuilder(
                        ex.getStatusCode(),
                        "Weather client api call error",
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorGenericResponse> handleNetworkException(ResourceAccessException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(
                        apiErrorBuilder(
                                HttpStatus.BAD_GATEWAY.value(),
                                "Upstream API Unreachable",
                                "Connection to the upstream is unreachable",
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public ResponseEntity<ErrorGenericResponse> handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                apiErrorBuilder(
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "The requested path does not exist",
                        request.getRequestURI()
                )
        );
    }


    private ErrorGenericResponse apiErrorBuilder(int status, String error, String message, String path) {
        return new ErrorGenericResponse(
                DateUtil.generateTimestampFormattedDate(),
                status,
                error,
                message,
                path
        );
    }
}
