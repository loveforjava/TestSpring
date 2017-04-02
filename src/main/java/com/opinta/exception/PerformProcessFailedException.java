package com.opinta.exception;

public class PerformProcessFailedException extends Exception {

    public PerformProcessFailedException() {
        super();
    }

    public PerformProcessFailedException(String message) {
        super(message);
    }

    public PerformProcessFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PerformProcessFailedException(Throwable cause) {
        super(cause);
    }
}
