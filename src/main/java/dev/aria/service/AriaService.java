package dev.aria.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AriaService {

    private final ChatClient chatClient;
    private final List<Message> history = new ArrayList<>();

    public AriaService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String chat(String userMessage) {
        history.add(new UserMessage(userMessage));

        List<Message> context = history.stream()
                .skip(Math.max(0, history.size() - 20))
                .toList();

        String response = chatClient.prompt()
                .messages(context)
                .call()
                .content();

        history.add(new AssistantMessage(response));
        return response;
    }

    public void clearHistory() {
        history.clear();
    }

    public int getHistorySize() {
        return history.size();
    }
}