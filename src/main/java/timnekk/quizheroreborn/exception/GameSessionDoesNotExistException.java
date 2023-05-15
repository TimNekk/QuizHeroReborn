package timnekk.quizheroreborn.exception;

public class GameSessionDoesNotExistException extends RuntimeException {

    public GameSessionDoesNotExistException() {
        super();
    }

    public GameSessionDoesNotExistException(String message) {
        super(message);
    }

}
