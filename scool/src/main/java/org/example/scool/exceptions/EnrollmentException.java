package org.example.scool.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EnrollmentException extends RuntimeException {
    public EnrollmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
