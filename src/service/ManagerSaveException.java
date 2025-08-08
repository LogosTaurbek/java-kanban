package service;

public class ManagerSaveException extends RuntimeException {
    private String exceptionMessage;

    public ManagerSaveException() {

    }

    public ManagerSaveException(final String message) {
        super(message);
        this.exceptionMessage = message;
    }

    String getExceptionMessage() {
        return this.exceptionMessage;
    }
}
