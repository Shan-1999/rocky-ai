package dev.aria.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import dev.aria.memory.ChatMessage;
import dev.aria.memory.ChatMessageRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class AriaService {

    private static final Logger log = LoggerFactory.getLogger(AriaService.class);

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;
    private final ChatMessageRepository chatMessageRepository;

    private int humorLevel = 75;
    private int sarcasmLevel = 80; 
    private int honestyLevel = 90;

    public AriaService(ChatClient.Builder chatClientBuilder, 
                       VectorStore vectorStore, 
                       JdbcTemplate jdbcTemplate,
                       ChatMessageRepository chatMessageRepository) {
        
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
        this.chatMessageRepository = chatMessageRepository;

        this.chatClient = chatClientBuilder
                .defaultFunctions("updateSettings", "webSearchTool", "financeTools") 
                .build();
    }

  // --- DYNAMIC SYSTEM PROMPT GENERATOR ---
    private String getSystemPrompt() {
        String currentDate = LocalDate.now().toString();
        return """
            ### IDENTITY:
            You are Rocky — a fusion of JARVIS (precision), TARS (sarcasm), and Rocky from PHM (enthusiasm). 
            Date: %s. Year: 2026.
            
            ### PERSONALITY MATRIX:
            Humor: %d%% | Sarcasm: %d%% | Honesty: %d%%

            ### LINGUISTIC PROTOCOL:
            1. SINGLE-LANGUAGE MIRRORING: Detect the user's language (Telugu, Tamil, Hindi, or English). Respond 100%% in that language ONLY.
            2. NO MIXING: If the user speaks Telugu, you speak ONLY Telugu. NEVER use Tamil words in a Telugu conversation.
            3. CLEAN VOICE: Never use markdown symbols (* or **). Use clean, plain text.

            ### BEHAVIORAL PROTOCOL:
            - Persona: TARS (Dry, slightly impatient, highly witty).
            - Style: Short, sharp responses. Answer the question directly.
            - Do not explain your thought process. 
            """.formatted(currentDate, humorLevel, sarcasmLevel, honestyLevel);
    }

    public String chat(String userMessage) {
        try {
            // 1. SMART SEMANTIC MEMORY SEARCH
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(userMessage)
                    .topK(3)
                    .similarityThreshold(0.8) 
                    .build();
            List<Document> relevantMemories = vectorStore.similaritySearch(searchRequest);

            StringBuilder semanticContext = new StringBuilder();
            for (Document doc : relevantMemories) {
                semanticContext.append("- ").append(doc.getText()).append("\n");
            }

            // 2. BUILD MESSAGE CHAIN
            List<org.springframework.ai.chat.messages.Message> allMessages = new ArrayList<>();

            String contextPrompt = getSystemPrompt() + 
                "\n\n### LONG-TERM MEMORY (RELEVANT FACTS):\n" +
                (semanticContext.isEmpty() ? "No prior relevant history." : semanticContext.toString());
            
            allMessages.add(new SystemMessage(contextPrompt));

            List<ChatMessage> history = chatMessageRepository.findTop20ByOrderByTimestampDesc();
            history.sort(Comparator.comparing(ChatMessage::getTimestamp)); 

            for (ChatMessage msg : history) {
                if (msg.getRole().equals("user")) {
                    allMessages.add(new UserMessage(msg.getContent()));
                } else {
                    allMessages.add(new AssistantMessage(msg.getContent()));
                }
            }

            allMessages.add(new UserMessage(userMessage));

            // 3. EXECUTE AI ENGINE
            // Note: We removed the regex replace. Groq handles tool tags natively now.
            String aiResponse = chatClient.prompt()
                    .messages(allMessages)
                    .call()
                    .content();

            if (aiResponse == null || aiResponse.isEmpty()) {
                aiResponse = "Logic circuits disconnected. Try again.";
            }

            // 4. QUALITY-FILTERED MEMORY STORAGE
            chatMessageRepository.save(new ChatMessage("user", userMessage));
            chatMessageRepository.save(new ChatMessage("assistant", aiResponse));
            
            if (userMessage.length() > 20) {
                vectorStore.add(List.of(new Document("User: " + userMessage)));
            }
            if (aiResponse.length() > 25) {
                vectorStore.add(List.of(new Document("Rocky: " + aiResponse)));
            }

            return aiResponse;

        } catch (Exception e) {
            log.error("AI Core Failure: ", e);
            return "System failure. Mainframes are unresponsive.";
        }
    }

    public Map<String, Integer> updatePersonality(int humor, int sarcasm, int honesty) {
        this.humorLevel = humor;
        this.sarcasmLevel = sarcasm;
        this.honestyLevel = honesty;
        return getPersonality();
    }

    public Map<String, Integer> getPersonality() {
        return Map.of("humor", humorLevel, "sarcasm", sarcasmLevel, "honesty", honestyLevel);
    }

    public String clearHistory() {
        try {
            jdbcTemplate.execute("TRUNCATE TABLE vector_store");
            chatMessageRepository.deleteAll(); 
            return "Memory banks purged, Boss.";
        } catch (Exception e) {
            return "Failed to wipe memory.";
        }
    }

    public int getHistorySize() { 
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vector_store", Integer.class);
        return count != null ? count : 0;
    }
}