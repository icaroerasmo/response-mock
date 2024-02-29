package com.icaroerasmo.responsemock.exceptions;

public class MockResponseException extends RuntimeException {
    public MockResponseException(String message) {
        super(message);
    }
    public MockResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
