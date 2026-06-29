package labb1k5.ai_proxy.controller;


import labb1k5.ai_proxy.dto.AiRequest;
import labb1k5.ai_proxy.dto.AiResponse;
import labb1k5.ai_proxy.service.AiClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/ai")
public class AiController {

    private final AiClientService aiClientService;

    public AiController(AiClientService aiClientService) {
        this.aiClientService = aiClientService;
    }

@PostMapping("/request")
    public ResponseEntity<AiResponse>createAiRequest(@RequestBody AiRequest request) {
        AiResponse aiResponse = aiClientService.analyzeText(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aiResponse);
    }
}
