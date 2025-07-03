package service;

public class ManagerSaveException extends Exception {
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
