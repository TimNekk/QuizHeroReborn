package timnekk.quizheroreborn.quiz.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import timnekk.quizheroreborn.question.model.Question;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameSession {

    private String username;
    private Question currentQuestion;
    private int points;
    private int questionsAnswered;

}
