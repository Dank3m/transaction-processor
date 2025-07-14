package com.kinduberre.transactionprocessor.controller;

import com.kinduberre.transactionprocessor.dto.BulkTransactionRequest;
import com.kinduberre.transactionprocessor.dto.ProcessingResponse;
import com.kinduberre.transactionprocessor.service.TransactionProcessingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionProcessingService processingService;

    public TransactionController(TransactionProcessingService processingService) {
        this.processingService = processingService;
    }

    @PostMapping("/process")
    public ResponseEntity<ProcessingResponse> processTransactions(
            @Valid @RequestBody BulkTransactionRequest request) {

        ProcessingResponse response = processingService.processTransactionsBulk(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process-async")
    public ResponseEntity<String> processTransactionsAsync(
            @Valid @RequestBody BulkTransactionRequest request) {

        CompletableFuture<ProcessingResponse> future = processingService.processTransactionsAsync(request);

        return ResponseEntity.accepted()
                .body("Processing started. Session ID: " +
                        (request.getSessionId() != null ? request.getSessionId() : "generated"));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service is healthy");
    }
}
