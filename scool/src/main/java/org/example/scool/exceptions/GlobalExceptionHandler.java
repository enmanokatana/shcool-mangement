package org.example.scool.exceptions;


import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({
            EntityNotFoundException.class,
            DuplicateEntityException.class,
            EnrollmentException.class
    })
    public ResponseEntity<ErrorResponse> handleCustomExceptions(Exception ex, WebRequest request) {
        HttpStatus status = determineHttpStatus(ex);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(((ServletWebRequest)request).getRequest().getRequestURI())
                .build();

        log.error("Error occurred: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(error, status);
    }

    private HttpStatus determineHttpStatus(Exception ex) {
        if (ex instanceof EntityNotFoundException) return HttpStatus.NOT_FOUND;
        if (ex instanceof DuplicateEntityException) return HttpStatus.CONFLICT;
        if (ex instanceof EnrollmentException) return HttpStatus.BAD_REQUEST;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}