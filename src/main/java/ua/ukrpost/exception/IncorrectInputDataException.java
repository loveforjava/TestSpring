package ua.ukrpost.exception;

public class IncorrectInputDataException extends Exception {

    public IncorrectInputDataException() {
        super();
    }

    public IncorrectInputDataException(String message) {
        super(message);
    }

    public IncorrectInputDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectInputDataException(Throwable cause) {
        super(cause);
    }
}
