package timnekk.quizheroreborn.quiz;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timnekk.quizheroreborn.exception.model.StringExceptionResponse;
import timnekk.quizheroreborn.exception.model.StringMapExceptionResponse;
import timnekk.quizheroreborn.quiz.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/quiz")
@RequiredArgsConstructor
@Slf4j
public class QuizController {

    private final GameSessionService gameSessionService;

    @Operation(summary = "Start new game session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully started a new game session"),
            @ApiResponse(responseCode = "409", description = "Game session already exists",
                    content = @Content(schema = @Schema(implementation = StringExceptionResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = StringExceptionResponse.class)))
    })
    @PostMapping("/start")
    public ResponseEntity<GameSessionResponse> startGame() {
        return ResponseEntity.status(HttpStatus.OK).body(gameSessionService.startGameSession());
    }

    @Operation(summary = "Get current question")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched the current question"),
            @ApiResponse(responseCode = "404", description = "Game session does not exist",
                    content = @Content(schema = @Schema(implementation = StringExceptionResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = StringExceptionResponse.class)))
    })
    @GetMapping("/question")
    public ResponseEntity<QuestionResponse> getQuestion() {
        return ResponseEntity.status(HttpStatus.OK).body(gameSessionService.getCurrentQuestion());
    }

    @Operation(summary = "Submit answer for current question")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully submitted the answer"),
            @ApiResponse(responseCode = "400", description = "Answer is not valid",
                    content = @Content(schema = @Schema(implementation = StringMapExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Game session does not exist",
                    content = @Content(schema = @Schema(implementation = StringExceptionResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = StringExceptionResponse.class)))
    })
    @PostMapping("/answer")
    public ResponseEntity<AnswerResponse> submitAnswer(@Valid @RequestBody AnswerRequest answer) {
        return ResponseEntity.status(HttpStatus.OK).body(gameSessionService.submitAnswer(answer));
    }

    @Operation(summary = "Get current game session details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched the current game session"),
            @ApiResponse(responseCode = "404", description = "Game session does not exist",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = StringExceptionResponse.class)))
    })
    @GetMapping("/session")
    public ResponseEntity<GameSessionResponse> getSession() {
        return ResponseEntity.status(HttpStatus.OK).body(gameSessionService.getGameSession());
    }

    @Operation(summary = "Finish the current game session. The points from the session will be added to the user's total points")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully finished the current game session"),
            @ApiResponse(responseCode = "404", description = "Game session does not exist",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = StringExceptionResponse.class)))
    })
    @PostMapping("/finish")
    public ResponseEntity<GameSessionResponse> finishGame() {
        return ResponseEntity.status(HttpStatus.OK).body(gameSessionService.finishGameSession());
    }

}