package com.kinduberre.transactionprocessor.service;

import com.kinduberre.transactionprocessor.dto.BulkTransactionRequest;
import com.kinduberre.transactionprocessor.dto.ProcessingResponse;
import com.kinduberre.transactionprocessor.dto.ProcessingStats;
import com.kinduberre.transactionprocessor.dto.TransactionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class TransactionProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessingService.class);


    private final LLMService llmService;

    private final TransactionStatsService statsService;

    public TransactionProcessingService(LLMService llmService, TransactionStatsService statsService) {
        this.llmService = llmService;
        this.statsService = statsService;
    }

    public ProcessingResponse processTransactionsBulk(BulkTransactionRequest request) {
        String sessionId = request.getSessionId() != null ? request.getSessionId() : UUID.randomUUID().toString();

        logger.info("Processing {} transaction messages for session {}", request.getMessages().size(), sessionId);

        try {
            List<TransactionData> transactions = llmService.processTransactionMessages(request.getMessages());

            ProcessingResponse response = new ProcessingResponse(sessionId, transactions);
            response.setTotalMessages(request.getMessages().size());
            response.setSuccessfullyProcessed(transactions.size());

            // Calculate stats
            ProcessingStats stats = statsService.calculateStats(transactions);
            response.setStats(stats);

            logger.info("Successfully processed {} transactions for session {}", transactions.size(), sessionId);

            return response;

        } catch (Exception e) {
            logger.error("Error processing transactions for session {}: {}", sessionId, e.getMessage());
            throw new RuntimeException("Failed to process transactions", e);
        }
    }

    @Async
    public CompletableFuture<ProcessingResponse> processTransactionsAsync(BulkTransactionRequest request) {
        return CompletableFuture.completedFuture(processTransactionsBulk(request));
    }
}
