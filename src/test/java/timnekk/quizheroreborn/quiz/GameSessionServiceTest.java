package timnekk.quizheroreborn.quiz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UserDetails;
import timnekk.quizheroreborn.auth.CurrentUserComponent;
import timnekk.quizheroreborn.exception.GameSessionAlreadyExistsException;
import timnekk.quizheroreborn.exception.GameSessionDoesNotExistException;
import timnekk.quizheroreborn.question.QuestionService;
import timnekk.quizheroreborn.question.model.Question;
import timnekk.quizheroreborn.quiz.model.*;
import timnekk.quizheroreborn.user.UserRepository;
import timnekk.quizheroreborn.user.model.Role;
import timnekk.quizheroreborn.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameSessionServiceTest {

    @Mock
    private RedisTemplate<String, GameSession> redisTemplate;

    @Mock
    private ValueOperations<String, GameSession> valueOperations;

    @Mock
    private QuestionService questionService;

    @Mock
    private CurrentUserComponent currentUserComponent;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GameSessionService gameSessionService;

    private User testUser;
    private GameSession gameSession;
    private Question question;

    @BeforeEach
    public void setUp() {
        testUser = User.builder()
                .username("user1")
                .points(100)
                .build();

        UserDetails userDetails = User.builder()
                .username(testUser.getUsername())
                .password("password")
                .role(Role.USER)
                .build();

        question = Question.builder()
                .answer("testAnswer")
                .difficulty(2)
                .value("testQuestion")
                .build();

        gameSession = GameSession.builder()
                .currentQuestion(question)
                .questionsAnswered(1)
                .points(0)
                .username(testUser.getUsername())
                .build();

        when(currentUserComponent.getCurrentUser()).thenReturn(userDetails);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void startGameSession_WhenNoCurrentGameSessionExists_ReturnsGameSessionResponse() {
        // Arrange
        when(valueOperations.get(anyString())).thenReturn(null);
        when(questionService.getQuestion()).thenReturn(question);

        // Act
        GameSessionResponse result = gameSessionService.startGameSession();

        // Assert
        assertEquals(0, result.getPoints());
        assertEquals(0, result.getQuestionsAnswered());
    }

    @Test
    void startGameSession_WhenCurrentGameSessionExists_ThrowsGameSessionAlreadyExistsException() {
        // Arrange
        when(valueOperations.get(anyString())).thenReturn(gameSession);

        // Act & Assert
        assertThrows(GameSessionAlreadyExistsException.class, () -> gameSessionService .startGameSession());
    }

    @Test
    void getCurrentQuestion_WhenGameSessionExists_ReturnsQuestionResponse() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(gameSession);

        // Act
        QuestionResponse response = gameSessionService.getCurrentQuestion();

        // Assert
        assertEquals(gameSession.getCurrentQuestion().getValue(), response.getQuestion());
        assertEquals(gameSession.getCurrentQuestion().getDifficulty(), response.getDifficulty());
    }

    @Test
    void getCurrentQuestion_WhenNoGameSessionExists_ThrowsGameSessionDoesNotExistException() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);

        // Act & Assert
        assertThrows(GameSessionDoesNotExistException.class, () -> gameSessionService.getCurrentQuestion());
    }

    @Test
    void submitAnswer_WhenAnswerIsCorrect_IncreasesPointsAndReturnsAnswerResponse() {
        // Arrange
        AnswerRequest answerRequest = new AnswerRequest();
        String expectedAnswer = gameSession.getCurrentQuestion().getAnswer();
        answerRequest.setAnswer(expectedAnswer);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(gameSession);

        // Act
        AnswerResponse response = gameSessionService.submitAnswer(answerRequest);

        // Assert
        assertTrue(response.isCorrect());
        assertEquals(expectedAnswer, response.getCorrectAnswer());
    }

    @Test
    void submitAnswer_WhenAnswerIsIncorrect_DoesNotIncreasePointsAndReturnsIncorrectAnswerResponse() {
        // Arrange
        AnswerRequest answerRequest = new AnswerRequest();
        answerRequest.setAnswer("wrongAnswer");
        String expectedAnswer = gameSession.getCurrentQuestion().getAnswer();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(gameSession);

        // Act
        AnswerResponse response = gameSessionService.submitAnswer(answerRequest);

        // Assert
        assertFalse(response.isCorrect());
        assertEquals(expectedAnswer, response.getCorrectAnswer());
        assertEquals(0, gameSession.getPoints());
    }

    @Test
    void submitAnswer_WhenNoGameSessionExists_ThrowsGameSessionDoesNotExistException() {
        // Arrange
        AnswerRequest answerRequest = new AnswerRequest();
        answerRequest.setAnswer("anyAnswer");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);

        // Act and Assert
        assertThrows(GameSessionDoesNotExistException.class, () -> gameSessionService.submitAnswer(answerRequest));
    }


    @Test
    void getGameSession_WhenGameSessionExists_ReturnsGameSessionResponse() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(gameSession);

        // Act
        GameSessionResponse response = gameSessionService.getGameSession();

        // Assert
        assertEquals(gameSession.getPoints(), response.getPoints());
        assertEquals(gameSession.getQuestionsAnswered(), response.getQuestionsAnswered());
    }

    @Test
    void getGameSession_WhenNoGameSessionExists_ThrowsGameSessionDoesNotExistException() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);

        // Act and Assert
        assertThrows(GameSessionDoesNotExistException.class, () -> gameSessionService.getGameSession());
    }

    @Test
    void finishGameSession_WhenGameSessionExists_ReturnsGameSessionResponseAndDeletesSession() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(gameSession);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        // Act
        GameSessionResponse response = gameSessionService.finishGameSession();

        // Assert
        assertEquals(gameSession.getPoints(), response.getPoints());
        assertEquals(gameSession.getQuestionsAnswered(), response.getQuestionsAnswered());
        verify(redisTemplate, times(1)).delete(anyString());
    }

    @Test
    void finishGameSession_WhenNoGameSessionExists_ThrowsGameSessionDoesNotExistException() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);

        // Act and Assert
        assertThrows(GameSessionDoesNotExistException.class, () -> gameSessionService.finishGameSession());
    }

}
