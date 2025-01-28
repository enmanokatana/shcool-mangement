package org.example.scool.exceptions;

public class DataFetchException extends RuntimeException {
    public DataFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}