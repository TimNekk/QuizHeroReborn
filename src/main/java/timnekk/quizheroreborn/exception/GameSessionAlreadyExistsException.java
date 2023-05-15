package timnekk.quizheroreborn.exception;

public class GameSessionAlreadyExistsException extends RuntimeException{

    public GameSessionAlreadyExistsException() {
        super();
    }

    public GameSessionAlreadyExistsException(String message) {
        super(message);
    }

}
