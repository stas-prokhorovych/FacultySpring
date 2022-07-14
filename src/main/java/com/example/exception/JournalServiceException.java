package com.example.exception;

public class JournalServiceException extends RuntimeException {
    public JournalServiceException() {
        super();
    }

    public JournalServiceException(String message) {
        super(message);
    }

}
