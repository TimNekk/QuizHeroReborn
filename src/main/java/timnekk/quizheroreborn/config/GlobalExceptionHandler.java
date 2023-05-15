package timnekk.quizheroreborn.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import timnekk.quizheroreborn.exception.GameSessionAlreadyExistsException;
import timnekk.quizheroreborn.exception.GameSessionDoesNotExistException;
import timnekk.quizheroreborn.exception.QuestionsFetchFailedException;
import timnekk.quizheroreborn.exception.UsernameConflictException;
import timnekk.quizheroreborn.exception.model.StringExceptionResponse;
import timnekk.quizheroreborn.exception.model.StringMapExceptionResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StringMapExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new StringMapExceptionResponse(errors));
    }

    @ExceptionHandler(UsernameConflictException.class)
    public ResponseEntity<StringExceptionResponse> handleUsernameConflictException(UsernameConflictException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new StringExceptionResponse(ex.getMessage()));
    }

    @ExceptionHandler(QuestionsFetchFailedException.class)
    public ResponseEntity<StringExceptionResponse> handleQuestionsFetchFailedException(QuestionsFetchFailedException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new StringExceptionResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<StringExceptionResponse> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new StringExceptionResponse(ex.getMessage()));
    }

    @ExceptionHandler(GameSessionDoesNotExistException.class)
    public ResponseEntity<StringExceptionResponse> handleGameSessionDoesNotExistException(GameSessionDoesNotExistException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new StringExceptionResponse(ex.getMessage()));
    }

    @ExceptionHandler(GameSessionAlreadyExistsException.class)
    public ResponseEntity<StringExceptionResponse> handleGameSessionAlreadyExistException(GameSessionAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new StringExceptionResponse(ex.getMessage()));
    }

}