package labb1k5.ai_proxy.service;

import labb1k5.ai_proxy.client.GroqClient;
import labb1k5.ai_proxy.dto.AiRequest;
import labb1k5.ai_proxy.dto.AiResponse;
import labb1k5.ai_proxy.parser.AiResponseParser;
import labb1k5.ai_proxy.prompt.PromptStrategy;
import org.springframework.stereotype.Service;

@Service
public class AiClientService {

    private final PromptStrategy promptStrategy;
    private final GroqClient groqClient;
    private final AiResponseParser responseParser;

    public AiClientService(PromptStrategy promptStrategy,
                           GroqClient groqClient,
                           AiResponseParser responseParser) {
        this.promptStrategy = promptStrategy;
        this.groqClient = groqClient;
        this.responseParser = responseParser;
    }

    public AiResponse analyzeText(AiRequest aiRequest) {
        String payloadResult = groqClient.send(promptStrategy.buildPayload(aiRequest));
        return responseParser.parse(payloadResult);
    }
}