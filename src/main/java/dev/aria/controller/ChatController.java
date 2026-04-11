package dev.aria.controller;

import dev.aria.service.AriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/aria")
@CrossOrigin(origins = "*")
public class ChatController {

    private final AriaService ariaService;

    public ChatController(AriaService ariaService) {
        this.ariaService = ariaService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(
            @RequestBody Map<String, String> request) {

        String userMessage = request.get("message");

        if (userMessage == null || userMessage.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Message cannot be empty"));
        }

        String response = ariaService.chat(userMessage);
        return ResponseEntity.ok(Map.of("response", response));
    }

    @DeleteMapping("/memory")
    public ResponseEntity<Map<String, String>> clearMemory() {
        ariaService.clearHistory();
        return ResponseEntity.ok(
                Map.of("status", "Memory cleared. Fresh start, Shanmuga."));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        return ResponseEntity.ok(Map.of(
                "status", "online",
                "agent", "ARIA",
                "messages_in_memory", ariaService.getHistorySize()
        ));
    }
}