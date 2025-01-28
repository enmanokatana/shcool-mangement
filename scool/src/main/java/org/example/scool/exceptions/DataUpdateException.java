package org.example.scool.exceptions;

public class DataUpdateException extends RuntimeException {
    public DataUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}