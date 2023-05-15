package timnekk.quizheroreborn.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import timnekk.quizheroreborn.quiz.model.GameSession;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisConnectionFactory redisConnectionFactory;

    @Bean
    public RedisTemplate<String, GameSession> redisTemplate() {
        RedisTemplate<String, GameSession> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(GameSession.class));
        return template;
    }
}
