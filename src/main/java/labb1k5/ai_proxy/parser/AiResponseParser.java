package labb1k5.ai_proxy.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import labb1k5.ai_proxy.dto.AiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class AiResponseParser {

    private static final Logger log = LoggerFactory.getLogger(AiResponseParser.class);
    private static final AiResponse FALLBACK = new AiResponse(null, 0, BigDecimal.ZERO);

    private final ObjectMapper objectMapper;
    private final Validator validator;

    public AiResponseParser(ObjectMapper objectMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    public AiResponse parse(String rawResponse) {
        if (rawResponse == null) {
            return FALLBACK;
        }

        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            String jsonText = root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
            AiResponse parsed = objectMapper.readValue(jsonText, AiResponse.class);

            Set<ConstraintViolation<AiResponse>> violations = validator.validate(parsed);
            if (!violations.isEmpty()) {
                log.warn("AI-svar bröt mot valideringsregler: {}", violations);
                return FALLBACK;
            }
            return parsed;

        } catch (Exception e) {
            log.warn("Kunde inte parsa AI-svar: {}", e.getMessage());
            return FALLBACK;
        }
    }
}
