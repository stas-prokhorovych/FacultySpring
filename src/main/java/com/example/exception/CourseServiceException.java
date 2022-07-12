package com.example.exception;

public class CourseServiceException extends RuntimeException {
    public CourseServiceException() {
        super();
    }

    public CourseServiceException(String message) {
        super(message);
    }

}