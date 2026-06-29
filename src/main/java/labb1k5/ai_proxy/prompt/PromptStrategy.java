package labb1k5.ai_proxy.prompt;

import labb1k5.ai_proxy.dto.AiRequest;

import java.util.Map;

public interface PromptStrategy {
    Map<String, Object> buildPayload(AiRequest request);
}