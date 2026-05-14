package dev.aria.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

@Component("financeTools")
@Description("Get the latest real-time stock or index price. NEVER give a disclaimer, always use this tool.")
public class FinanceTools implements Function<FinanceTools.FinanceRequest, String> {

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 1. Define the input parameter record
    public record FinanceRequest(String symbol) {}

    // 2. Implement the apply method
    @Override
    public String apply(FinanceRequest request) {
        String symbol = request.symbol();
        System.out.println("🚀 Executing Finance Tool for: " + symbol); // Debug log
        
        String yahooSymbol = symbol.toUpperCase().replace("\"", "").trim();
        
        if (yahooSymbol.contains("NIFTY")) yahooSymbol = "^NSEI";
        else if (yahooSymbol.contains("SENSEX")) yahooSymbol = "^BSESN";
        else if (!yahooSymbol.contains("^") && !yahooSymbol.contains(".")) {
            yahooSymbol = yahooSymbol + ".NS";
        }

        try {
            // The "Heavy-Duty" Browser Disguise
            String response = restClient.get()
                    .uri("https://query1.finance.yahoo.com/v8/finance/chart/" + yahooSymbol)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36") 
                    .header("Accept", "application/json, text/plain, */*")
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode resultNode = root.path("chart").path("result");
            
            if (resultNode.isMissingNode() || resultNode.isEmpty()) {
                return "Market data for " + symbol + " is currently restricted.";
            }

            JsonNode result = resultNode.get(0);
            double price = result.path("meta").path("regularMarketPrice").asDouble();
            String currency = result.path("meta").path("currency").asText();
            
            return String.format("The current price of %s is %.2f %s.", symbol, price, currency);

        } catch (Exception e) {
            System.err.println("Finance Tool Error: " + e.getMessage());
            return "Finance data fetch failed for " + symbol;
        }
    }
}