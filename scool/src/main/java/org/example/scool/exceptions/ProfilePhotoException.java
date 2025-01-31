package org.example.scool.exceptions;

public class ProfilePhotoException extends RuntimeException {
    public ProfilePhotoException(String message) {
        super(message);
    }

    public ProfilePhotoException(String message, Throwable cause) {
        super(message, cause);
    }
}
