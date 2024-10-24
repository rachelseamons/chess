package service;

public class ChessException extends Exception {
    private final int status;

    public ChessException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
