package timnekk.quizheroreborn.quiz;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import timnekk.quizheroreborn.auth.JwtService;
import timnekk.quizheroreborn.exception.GameSessionAlreadyExistsException;
import timnekk.quizheroreborn.exception.GameSessionDoesNotExistException;
import timnekk.quizheroreborn.quiz.model.AnswerRequest;
import timnekk.quizheroreborn.quiz.model.AnswerResponse;
import timnekk.quizheroreborn.quiz.model.GameSessionResponse;
import timnekk.quizheroreborn.quiz.model.QuestionResponse;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(QuizController.class)
@AutoConfigureMockMvc(addFilters = false)
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameSessionService gameSessionService;

    private GameSessionResponse gameSessionResponse;
    private QuestionResponse questionResponse;
    private AnswerResponse answerResponse;
    private AnswerRequest answerRequest;

    @BeforeEach
    public void setUp() {
        gameSessionResponse = GameSessionResponse.builder()
                .points(2)
                .questionsAnswered(3)
                .build();

        questionResponse = QuestionResponse.builder()
                .question("What is the capital of Australia?")
                .difficulty(100)
                .build();

        answerResponse = AnswerResponse.builder()
                .isCorrect(true)
                .correctAnswer("Canberra")
                .build();

        answerRequest = AnswerRequest.builder()
                .answer("Canberra")
                .build();
    }

    @Test
    void startGame_WhenGameSessionIsNotStarted_ReturnsOk() throws Exception {
        when(gameSessionService.startGameSession()).thenReturn(gameSessionResponse);

        mockMvc.perform(post("/api/v1/quiz/start")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points", is(gameSessionResponse.getPoints())))
                .andExpect(jsonPath("$.questionsAnswered", is(gameSessionResponse.getQuestionsAnswered())));
    }

    @Test
    void startGame_WhenGameSessionIsStarted_ReturnsConflict() throws Exception {
        when(gameSessionService.startGameSession()).thenThrow(new GameSessionAlreadyExistsException());

        mockMvc.perform(post("/api/v1/quiz/start")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void getQuestion_WhenGameSessionIsStarted_ReturnsOk() throws Exception {
        when(gameSessionService.getCurrentQuestion()).thenReturn(questionResponse);

        mockMvc.perform(get("/api/v1/quiz/question")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question", is(questionResponse.getQuestion())))
                .andExpect(jsonPath("$.difficulty", is(questionResponse.getDifficulty())));
    }

    @Test
    void getQuestion_WhenGameSessionIsNotStarted_ReturnsNotFound() throws Exception {
        when(gameSessionService.getCurrentQuestion()).thenThrow(new GameSessionDoesNotExistException());

        mockMvc.perform(get("/api/v1/quiz/question")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void submitAnswer_WhenAnswerIsNotValid_ReturnsBadRequest() throws Exception {
        AnswerRequest answerRequest = new AnswerRequest();
        answerRequest.setAnswer("");

        mockMvc.perform(post("/api/v1/quiz/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitAnswer_WhenAnswerIsValid_ReturnsOk() throws Exception {
        when(gameSessionService.submitAnswer(answerRequest)).thenReturn(answerResponse);

        mockMvc.perform(post("/api/v1/quiz/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct", is(answerResponse.isCorrect())))
                .andExpect(jsonPath("$.correctAnswer", is(answerResponse.getCorrectAnswer())));
    }

    @Test
    void submitAnswer_WhenGameSessionIsNotStarted_ReturnsNotFound() throws Exception {
        when(gameSessionService.submitAnswer(any(AnswerRequest.class))).thenThrow(new GameSessionDoesNotExistException());

        mockMvc.perform(post("/api/v1/quiz/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSession_WhenGameSessionIsStarted_ReturnsOk() throws Exception {
        when(gameSessionService.getGameSession()).thenReturn(gameSessionResponse);

        mockMvc.perform(get("/api/v1/quiz/session")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points", is(gameSessionResponse.getPoints())))
                .andExpect(jsonPath("$.questionsAnswered", is(gameSessionResponse.getQuestionsAnswered())));
    }

    @Test
    void getSession_WhenGameSessionIsNotStarted_ReturnsNotFound() throws Exception {
        when(gameSessionService.getGameSession()).thenThrow(new GameSessionDoesNotExistException());

        mockMvc.perform(get("/api/v1/quiz/session")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void finishGame_WhenGameSessionIsStarted_ReturnsOk() throws Exception {
        when(gameSessionService.finishGameSession()).thenReturn(gameSessionResponse);

        mockMvc.perform(post("/api/v1/quiz/finish")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points", is(gameSessionResponse.getPoints())))
                .andExpect(jsonPath("$.questionsAnswered", is(gameSessionResponse.getQuestionsAnswered())));
    }

    @Test
    void finishGame_WhenGameSessionIsNotStarted_ReturnsNotFound() throws Exception {
        when(gameSessionService.finishGameSession()).thenThrow(new GameSessionDoesNotExistException());

        mockMvc.perform(post("/api/v1/quiz/finish")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}

