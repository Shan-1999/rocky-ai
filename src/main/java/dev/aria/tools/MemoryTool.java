package dev.aria.tools;

import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component("memoryTool")
public class MemoryTool {

    private final VectorStore vectorStore;

    public MemoryTool(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Tool(name = "recall", description = "Search past conversations to recall facts.")
    
   @SuppressWarnings("null") 
    public String searchMemory(
        @ToolParam(description = "The topic or keyword to search for") String query
    ) {
        String queryText = query.replace("\"", "").trim();

        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(queryText)
                        .topK(3)
                        .build()
        );

        if (results.isEmpty()) return "I don't have any specific memories about that yet.";

        return "Based on our past conversations:\n" + results.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));
    }
}