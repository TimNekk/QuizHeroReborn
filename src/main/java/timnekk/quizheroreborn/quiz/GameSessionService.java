package timnekk.quizheroreborn.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import timnekk.quizheroreborn.auth.CurrentUserComponent;
import timnekk.quizheroreborn.exception.GameSessionAlreadyExistException;
import timnekk.quizheroreborn.exception.GameSessionDoesNotExistException;
import timnekk.quizheroreborn.question.model.Question;
import timnekk.quizheroreborn.question.QuestionService;
import timnekk.quizheroreborn.quiz.model.*;
import timnekk.quizheroreborn.user.model.User;
import timnekk.quizheroreborn.user.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameSessionService {

    private final RedisTemplate<String, GameSession> redisTemplate;
    private final QuestionService questionService;
    private final CurrentUserComponent currentUserComponent;
    private final UserRepository userRepository;

    private static final String GAME_SESSION_PREFIX = "game-session-";

    public GameSessionResponse startGameSession() {
        if (getCurrentGameSession().isPresent()) {
            throw new GameSessionAlreadyExistException("Game session already exist");
        }

        GameSession gameSession = createGameSession();

        return GameSessionResponse.builder()
                .points(gameSession.getPoints())
                .questionCount(gameSession.getQuestionCount())
                .build();
    }

    public QuestionResponse getCurrentQuestion() {
        GameSession session = tryGetCurrentGameSession();
        Question currentQuestion = session.getCurrentQuestion();

        return QuestionResponse.builder()
                .question(currentQuestion.getValue())
                .difficulty(currentQuestion.getDifficulty())
                .build();
    }

    public AnswerResponse submitAnswer(AnswerRequest answerRequest) {
        GameSession session = tryGetCurrentGameSession();
        Question currentQuestion = session.getCurrentQuestion();

        boolean isCorrect = isAnswerCorrect(currentQuestion, answerRequest);
        if (isCorrect) {
            session.setPoints(session.getPoints() + currentQuestion.getDifficulty());
        }

        session.setQuestionCount(session.getQuestionCount() + 1);
        setNextQuestion(session);

        return AnswerResponse.builder()
                .isCorrect(isCorrect)
                .correctAnswer(currentQuestion.getAnswer())
                .build();
    }

    public GameSessionResponse getGameSession() {
        GameSession session = tryGetCurrentGameSession();

        return GameSessionResponse.builder()
                .points(session.getPoints())
                .questionCount(session.getQuestionCount())
                .build();
    }

    public GameSessionResponse finishGameSession() {
        GameSession session = tryGetCurrentGameSession();

        addPointsToUser(session);

        redisTemplate.delete(getCurrentGameSessionId());

        return GameSessionResponse.builder()
                .points(session.getPoints())
                .questionCount(session.getQuestionCount())
                .build();
    }

    private void setNextQuestion(GameSession gameSession) {
        Question nextQuestion = questionService.getQuestion();
        gameSession.setCurrentQuestion(nextQuestion);

        ValueOperations<String, GameSession> ops = getGameSessionOps();
        String sessionId = getCurrentGameSessionId();
        ops.set(sessionId, gameSession);
    }

    private Optional<GameSession> getCurrentGameSession() {
        String sessionId = getCurrentGameSessionId();

        ValueOperations<String, GameSession> ops = getGameSessionOps();
        GameSession gameSession = ops.get(sessionId);

        return Optional.ofNullable(gameSession);
    }

    private GameSession tryGetCurrentGameSession() throws GameSessionDoesNotExistException {
        Optional<GameSession> gameSession = getCurrentGameSession();

        if (gameSession.isEmpty()) {
            throw new GameSessionDoesNotExistException("Game session does not exist");
        }

        return gameSession.get();
    }

    private GameSession createGameSession() {
        String sessionId = getCurrentGameSessionId();

        GameSession gameSession = GameSession.builder()
                .username(currentUserComponent.getCurrentUser().getUsername())
                .currentQuestion(questionService.getQuestion())
                .build();

        ValueOperations<String, GameSession> ops = getGameSessionOps();
        ops.set(sessionId, gameSession);

        return gameSession;
    }

    private ValueOperations<String, GameSession> getGameSessionOps() {
        return redisTemplate.opsForValue();
    }

    private String getCurrentGameSessionId() {
        UserDetails currentUser = currentUserComponent.getCurrentUser();
        return GAME_SESSION_PREFIX + currentUser.getUsername();
    }

    private boolean isAnswerCorrect(Question question, AnswerRequest answerRequest) {
        return question.getAnswer().equalsIgnoreCase(answerRequest.getAnswer());
    }

    private void addPointsToUser(GameSession session) {
        User user = userRepository.findByUsername(session.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setPoints(user.getPoints() + session.getPoints());
        userRepository.save(user);
    }

}