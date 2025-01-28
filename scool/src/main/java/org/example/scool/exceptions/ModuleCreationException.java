package org.example.scool.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ModuleCreationException extends RuntimeException {
    public ModuleCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}