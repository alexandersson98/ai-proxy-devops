package labb1k5.ai_proxy.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import labb1k5.ai_proxy.dto.AiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AiResponseParserTest {

    private AiResponseParser parser;

    @BeforeEach
    void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        parser = new AiResponseParser(objectMapper, validator);
    }

    @Test
    void returnsFallbackWhenAiReturnsTextInsteadOfJson() {
        String hallucinated =
                "{\"choices\":[{\"message\":{\"content\":\"hejsan hoppsan lillebror\"}}]}";

        AiResponse result = parser.parse(hallucinated);

        assertNull(result.product());
        assertEquals(0, result.quantity());
    }

    @Test
    void returnsFallbackWhenResponseIsNotEvenJson() {
        AiResponse result = parser.parse("trasig");

        assertNull(result.product());
    }
}