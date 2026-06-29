package labb1k5.ai_proxy.prompt;


import labb1k5.ai_proxy.dto.AiRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OrderExtractionPrompt implements PromptStrategy {

    private static final String SYSTEM_PROMPT = """
              You are a structured data extraction assistant. Your sole purpose is to extract order information from unstructured text.

              Extract the following fields:
                - product: the name of the product (String)
                - quantity: the number of items (integer)
                - price: the price per unit (decimal number)
  
              Rules you must follow:
                1. Return ONLY a raw JSON object. No markdown, no code blocks, no explanations.
                2. Do not include any text before or after the JSON.
                3. If a field cannot be determined from the text, use null for strings and 0 for numbers.
                4. The response must exactly match this schema: {"product": "string", "quantity": integer, "price": decimal}.
                5. Never round, truncate or modify numerical values. Extract the exact number as written in the text.
              """;

    @Override
    public Map<String, Object> buildPayload(AiRequest request) {
        return Map.of(
                "model", "llama-3.3-70b-versatile",
                "temperature", 0.1,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", request.userInput())
                )
        );
    }
}