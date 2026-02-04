package com.Ypisds.eservices.exception;

public class SameServiceExistsException extends RuntimeException {
    public SameServiceExistsException(String message) {
        super(message);
    }
}
