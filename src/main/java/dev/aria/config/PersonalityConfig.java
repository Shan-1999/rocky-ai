package dev.aria.config;

import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Lazy; // <-- ADD THIS IMPORT

import dev.aria.service.AriaService;

@Configuration
public class PersonalityConfig {

    private static final Logger log = LoggerFactory.getLogger(PersonalityConfig.class);

    public record PersonalityRequest(int humor, int sarcasm, int honesty) {}

    @Bean
    @Description("Use this tool ONLY when the user explicitly asks to change your humor, sarcasm, or honesty settings. Values must be between 0 and 100.")
    // 👇 ADD @Lazy RIGHT HERE 👇
    public Function<PersonalityRequest, String> updateSettings(@Lazy AriaService ariaService) {
        return request -> {
            log.info("🤖 TARS Protocol: Updating settings -> Humor:{}, Sarcasm:{}, Honesty:{}", 
                    request.humor(), request.sarcasm(), request.honesty());
            
            ariaService.updatePersonality(request.humor(), request.sarcasm(), request.honesty());
            
            return "System settings updated. Acknowledge the new settings to the user using your newly adjusted personality.";
        };
    }
}