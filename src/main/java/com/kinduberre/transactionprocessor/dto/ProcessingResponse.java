package com.kinduberre.transactionprocessor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
public class ProcessingResponse {
    private String sessionId;
    private LocalDateTime processedAt;
    private int totalMessages;
    private int successfullyProcessed;
    private List<TransactionData> transactions;
    private List<ProcessingError> errors;
    private ProcessingStats stats;

    public ProcessingResponse(String sessionId, List<TransactionData> transactions) {
        this.sessionId = sessionId;
        this.processedAt = LocalDateTime.now();
        this.transactions = transactions;
        this.totalMessages = transactions.size();
        this.successfullyProcessed = transactions.size();
    }
}
