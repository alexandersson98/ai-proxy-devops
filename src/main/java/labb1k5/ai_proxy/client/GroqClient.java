package labb1k5.ai_proxy.client;

import labb1k5.ai_proxy.config.RestClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static java.lang.Thread.sleep;

@Component
public class GroqClient {
    private static final Logger log = LoggerFactory.getLogger(GroqClient.class);
    private static final int MAX_RETRIES =3;

    private final RestClient restClient;

    public GroqClient(RestClient restClient){
        this.restClient = restClient;
    }

    public String send(Map<String, Object> payload){
        long delay = 1000;

        for (int i = 0; i < MAX_RETRIES; i++) {
            ResponseEntity<String> response = restClient.post()
                    .uri("/openai/v1/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .onStatus(status -> status.value() == 429, (request, res) -> { })
                    .toEntity(String.class);


            if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS){
                log.warn("Fick 429 - för många anrop! Väntar {}ms innan nästa försök ({}/{})",
                        delay, i + 1, MAX_RETRIES);
                sleep(delay);
                delay *= 2;
                continue;
            }
            return response.getBody();
    }
        log.warn("Gav upp efter {} försök pga upprepade 429-svar", MAX_RETRIES);
        return null;
    }


        private void sleep(long ms){
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e ) {
                Thread.currentThread().interrupt();
            }
        }
}
