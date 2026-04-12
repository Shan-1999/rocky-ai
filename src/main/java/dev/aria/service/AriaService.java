package dev.aria.service;

import dev.aria.memory.ChatMessage;
import dev.aria.memory.ChatMessageRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;

@Service
public class AriaService {

    private final ChatClient chatClient;
    private final ChatMessageRepository repository;

    public AriaService(ChatClient chatClient, ChatMessageRepository repository) {
        this.chatClient = chatClient;
        this.repository = repository;
    }

    public String chat(String userMessage) {

        // 1. Save user message
        repository.save(new ChatMessage("user", userMessage));

        // 2. Fetch last 20 messages (DESC)
        List<ChatMessage> messages = repository.findTop20ByOrderByTimestampDesc();

        // 3. Reverse to correct order (old → new)
        Collections.reverse(messages);

        // 4. Convert to AI messages
   List<Message> context = repository
        .findTop20ByOrderByTimestampDesc()
        .stream()
        .sorted(Comparator.comparing(ChatMessage::getTimestamp))
        .map(msg -> (Message) (
                msg.getRole().equals("user")
                        ? new UserMessage(msg.getContent())
                        : new AssistantMessage(msg.getContent())
        ))
        .toList();
        // 5. Call AI
        String response = chatClient.prompt()
                .messages(context)
                .call()
                .content();

        // 6. Save AI response
        repository.save(new ChatMessage("assistant", response));

        return response;
    }

    public void clearHistory() {
        repository.deleteAll();
    }

    public int getHistorySize() {
        return (int) repository.count();
    }
}