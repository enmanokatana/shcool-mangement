package org.example.scool.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

public class ProfessorCreationException extends Throwable {
    public ProfessorCreationException(String unableToCreateProfessor, DataIntegrityViolationException ex) {
    }
}
