package timnekk.quizheroreborn.leaderboard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import timnekk.quizheroreborn.exception.model.StringExceptionResponse;
import timnekk.quizheroreborn.exception.model.StringMapExceptionResponse;
import timnekk.quizheroreborn.leaderboard.models.LeaderboardRequest;
import timnekk.quizheroreborn.leaderboard.models.LeaderboardResponse;

@RestController
@RequestMapping("/api/v1/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @Operation(summary = "Get leaderboard")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched the leaderboard"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = StringMapExceptionResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = StringExceptionResponse.class)))
    })
    @GetMapping
    public ResponseEntity<LeaderboardResponse> getLeaderboard(@Valid LeaderboardRequest request) {
        LeaderboardResponse response = leaderboardService.getLeaderboard(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
