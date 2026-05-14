package dev.aria.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AriaVoiceService {


    @Value("${elevenlabs.api-key}")
    private String apiKey;
    
    private final String VOICE_ID = "pNInz6obpg8nEmeWvj9L";
    private final RestClient restClient = RestClient.create();
    
    private static final Logger log = LoggerFactory.getLogger(AriaVoiceService.class);

    private final OpenAiAudioSpeechModel speechModel;
    private final OpenAiAudioTranscriptionModel transcriptionModel;

    public AriaVoiceService(OpenAiAudioSpeechModel speechModel, OpenAiAudioTranscriptionModel transcriptionModel) {
        this.speechModel = speechModel;
        this.transcriptionModel = transcriptionModel;
    }

    // THE EARS: Converts an uploaded audio file into Text
    public String transcribe(Resource audioResource) {
        try {
            log.info("👂 Listening to incoming audio stream...");
            AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource);
            String text = transcriptionModel.call(prompt).getResult().getOutput();
            log.info("📝 Transcribed: {}", text);
            return text;
        } catch (Exception e) {
            log.error("STT Failure: ", e);
            throw new RuntimeException("Failed to transcribe audio.");
        }
    }

    // THE MOUTH (ElevenLabs Hack)
    public byte[] speak(String text) {
        return restClient.post()
                .uri("https://api.elevenlabs.io/v1/text-to-speech/" + VOICE_ID)
                .header("xi-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                    "text", text,
                    "model_id", "eleven_turbo_v2_5",
                    "voice_settings", Map.of(
                        "stability", 0.4,       // Lower stability = more expressive/erratic
                        "similarity_boost", 0.8  // Higher similarity = cleaner voice
                    )
                ))
                .retrieve()
                .body(byte[].class);
    }
}