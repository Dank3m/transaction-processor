package com.kinduberre.transactionprocessor.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinduberre.transactionprocessor.dto.TransactionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LLMService {
    private static final Logger logger = LoggerFactory.getLogger(LLMService.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public LLMService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Value("${llm.api.url}")
    private String apiUrl;

    @Value("${llm.api.key}")
    private String apiKey;

    @Value("${llm.api.model}")
    private String model;

    @Value("${llm.api.max-tokens}")
    private int maxTokens;

    public List<TransactionData> processTransactionMessages(List<String> messages) {
        List<TransactionData> allTransactions = new ArrayList<>();

        // Process messages in batches to avoid token limits
        List<List<String>> batches = createBatches(messages, 10);

        for (int i = 0; i < batches.size(); i++) {
            List<String> batch = batches.get(i);
            logger.info("Processing batch {} of {}", i + 1, batches.size());

            try {
                List<TransactionData> batchResults = processBatch(batch);
                allTransactions.addAll(batchResults);
            } catch (Exception e) {
                logger.error("Error processing batch {}: {}", i + 1, e.getMessage());
                // Continue with other batches
            }
        }

        return allTransactions;
    }

    private List<TransactionData> processBatch(List<String> messages) {
        String prompt = buildPrompt(messages);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        try {
            String response = webClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseResponse(response);

        } catch (WebClientResponseException e) {
            logger.error("LLM API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to process transactions with LLM", e);
        } catch (Exception e) {
            logger.error("Unexpected error calling LLM API", e);
            throw new RuntimeException("Failed to process transactions", e);
        }
    }

    private String buildPrompt(List<String> messages) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Please extract and structure the following M-PESA transaction messages into JSON format. ");
        prompt.append("Each transaction should include transaction_id, status, type, amount, date, time, balance, costs, and any recipient/sender information. ");
        prompt.append("Return the result as a JSON array of transaction objects. ");
        prompt.append("Here are the messages:\n\n");

        for (int i = 0; i < messages.size(); i++) {
            prompt.append(String.format("Message %d: %s\n", i + 1, messages.get(i)));
        }

        prompt.append("\nPlease respond with only the JSON array, no additional text.");

        return prompt.toString();
    }

    private List<TransactionData> parseResponse(String response) {
        try {
            // Extract JSON from the response (assuming it's wrapped in a response object)
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            String content = (String) ((Map<String, Object>) ((List<?>) responseMap.get("content")).get(0)).get("text");

            // Parse the JSON array of transactions
            return objectMapper.readValue(content, new TypeReference<List<TransactionData>>() {});

        } catch (Exception e) {
            logger.error("Error parsing LLM response: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<List<String>> createBatches(List<String> messages, int batchSize) {
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < messages.size(); i += batchSize) {
            batches.add(messages.subList(i, Math.min(i + batchSize, messages.size())));
        }
        return batches;
    }
}
