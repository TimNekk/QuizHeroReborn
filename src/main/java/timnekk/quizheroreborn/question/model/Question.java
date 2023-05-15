package timnekk.quizheroreborn.question.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class Question {
    @JsonProperty("id")
    private int id;

    @JsonProperty("answer")
    private String answer;

    @JsonProperty("question")
    private String value;

    @JsonProperty("value")
    private int difficulty;

    @JsonProperty("airdate")
    private String airDate;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("category_id")
    private int categoryId;

    @JsonProperty("game_id")
    private int gameId;

    @JsonProperty("invalid_count")
    private Integer invalidCount;

    @JsonProperty("category")
    private Category category;
}