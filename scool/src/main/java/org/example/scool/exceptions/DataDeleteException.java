package org.example.scool.exceptions;

public class DataDeleteException extends Throwable {
    public DataDeleteException(String failedToDeleteStudent, Exception ex) {
        super(failedToDeleteStudent, ex);


    }
}
