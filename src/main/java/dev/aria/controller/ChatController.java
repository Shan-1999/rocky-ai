package dev.aria.controller;

import dev.aria.service.AriaService;
import dev.aria.service.AriaVoiceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/aria")
@CrossOrigin(origins = "*")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
   private final AriaVoiceService ariaVoiceService;
   private final AriaService ariaService;

    public ChatController(AriaService ariaService, AriaVoiceService ariaVoiceService) {
        this.ariaService = ariaService;
        this.ariaVoiceService = ariaVoiceService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");

        // 1. Validation
        if (userMessage == null || userMessage.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Message cannot be empty."));
        }

        try {
            log.info("Aria received message: {}", userMessage);
            
            // 2. Execution
            String response = ariaService.chat(userMessage);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "response", response
            ));

        } catch (Exception e) {
            // 3. Robust Error Catching for Tool Failures
            log.error("Rocky hit a snag processing tool call: ", e);

            // Handle the specific 400 errors we've been seeing in M6
            if (e.getMessage().contains("400") || e.getMessage().contains("tool_use_failed")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                            "error", "Tool Handshake Failed",
                            "message", "The AI generated an invalid tool request. Try rephrasing.",
                            "debug", e.getMessage()
                        ));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Execution Error",
                        "message", e.getMessage()
                    ));
        }
    }

    @DeleteMapping("/memory")
    public ResponseEntity<Map<String, String>> clearMemory() {
        try {
            ariaService.clearHistory();
            return ResponseEntity.ok(Map.of("status", "Memory cleared. Fresh start"));
        } catch (Exception e) {
            log.error("Failed to clear memory: ", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Memory wipe failed: " + e.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        // Return a more detailed status to help with your debugging
        return ResponseEntity.ok(Map.of(
                "status", "online",
                "agent", "ARIA-AI",
                "location", "Chennai",
                "milestone", "M6",
                "messages_in_memory", ariaService.getHistorySize()
        ));
    }

    @GetMapping("/settings")
    public ResponseEntity<Map<String, Integer>> getSettings() {
        return ResponseEntity.ok(ariaService.getPersonality());
    }

    @PostMapping("/settings")
    public ResponseEntity<Map<String, Object>> updateSettings(@RequestBody Map<String, Integer> settings) {
        try {
            int humor = settings.getOrDefault("humor", 75);
            int sarcasm = settings.getOrDefault("sarcasm", 60);
            int honesty = settings.getOrDefault("honesty", 90);
            
            Map<String, Integer> newSettings = ariaService.updatePersonality(humor, sarcasm, honesty);
            
            return ResponseEntity.ok(Map.of(
                "status", "Personality matrix updated successfully.",
                "settings", newSettings
            ));
        } catch (Exception e) {
            log.error("Failed to update settings: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid settings payload."));
        }
    }
@PostMapping(value = "/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> voiceChat(@RequestParam("audio") MultipartFile audioFile) {
        try {
            // 1. EAR: Transcribe using Groq
            String userMessage = ariaVoiceService.transcribe(audioFile.getResource());

            // 2. BRAIN: Process with TARS Personality
            String aiResponse = ariaService.chat(userMessage);

            // 3. MOUTH: Generate ElevenLabs Audio
            byte[] audioBytes = ariaVoiceService.speak(aiResponse);
            String base64Audio = Base64.getEncoder().encodeToString(audioBytes);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "youSaid", userMessage,
                    "rockySaid", aiResponse,
                    "audioBase64", base64Audio
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}