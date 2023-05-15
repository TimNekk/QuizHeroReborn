package timnekk.quizheroreborn.quiz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameSessionResponse {

    private int points;
    private int questionsAnswered;

}
