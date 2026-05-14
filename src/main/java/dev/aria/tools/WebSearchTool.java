package dev.aria.tools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.function.Function;

@Component("webSearchTool")
@Description("Search the web for real-time news, updates, facts, and general information.")
public class WebSearchTool implements Function<WebSearchTool.SearchRequest, String> {

    private final RestClient restClient = RestClient.create();
    
    @Value("${tavily.api.key}")
    private String apiKey;

    // The Input Record for the Tool
    public record SearchRequest(String query) {}

    @Override
    public String apply(SearchRequest request) {
        String queryText = request.query().replace("\"", "").trim();
        
        try {
            Map<?, ?> response = restClient.post()
                    .uri("https://api.tavily.com/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "api_key", apiKey,
                            "query", queryText,
                            "include_answer", true
                    ))
                    .retrieve()
                    .body(Map.class);

            return (response != null && response.get("answer") != null) 
                ? response.get("answer").toString() 
                : "I found results, but no direct answer was available.";
        } catch (Exception e) {
            return "Web search service is currently unavailable.";
        }
    }
}