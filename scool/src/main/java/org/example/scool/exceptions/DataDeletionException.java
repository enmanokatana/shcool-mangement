package org.example.scool.exceptions;

public class DataDeletionException extends RuntimeException {
    public DataDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}