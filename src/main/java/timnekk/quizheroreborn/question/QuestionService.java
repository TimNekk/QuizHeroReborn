package timnekk.quizheroreborn.question;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import timnekk.quizheroreborn.exception.QuestionsFetchFailedException;
import timnekk.quizheroreborn.question.model.Question;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Arrays;

@Service
public class QuestionService {

    private final WebClient webClient;
    private final Queue<Question> questionPool = new ConcurrentLinkedQueue<>();

    @Value("${app.questions-per-request}")
    private int questionsPerRequest;

    public QuestionService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://jservice.io/api/random").build();
    }

    public Question getQuestion() {
        if (!questionPool.isEmpty()) {
            return questionPool.poll();
        }

        try {
            Question[] questions = fetchQuestionsFromApi();
            questionPool.addAll(Arrays.asList(questions));
            return questionPool.poll();
        } catch (Exception error) {
            throw new QuestionsFetchFailedException(error.getMessage(), error);
        }
    }

    private Question[] fetchQuestionsFromApi() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("count", questionsPerRequest)
                        .build())
                .retrieve()
                .bodyToMono(Question[].class)
                .block();
    }

}