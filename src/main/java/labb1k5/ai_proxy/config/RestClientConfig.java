package labb1k5.ai_proxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {
    @Value("${groq.api.key:}")
    private String apiKey;

    @Bean
    public RestClient groqRestClient() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("CRITICAL: API key is missing.");
        }

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(8));

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl("https://api.groq.com")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
}
