package org.example.scool.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

public class StudentCreationException extends Exception {
    public StudentCreationException(String s, Exception ex) {
        super(s);
    }
}
