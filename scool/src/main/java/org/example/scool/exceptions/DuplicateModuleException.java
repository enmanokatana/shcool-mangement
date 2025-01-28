package org.example.scool.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateModuleException extends RuntimeException {
    public DuplicateModuleException(String message) {
        super(message);
    }
}