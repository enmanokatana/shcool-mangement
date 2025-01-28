package org.example.scool.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EnrollmentCreationException extends RuntimeException {
    public EnrollmentCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}