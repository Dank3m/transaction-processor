package com.kinduberre.transactionprocessor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class BulkTransactionRequest {
    @NotEmpty(message = "Messages list cannot be empty")
    private List<@NotBlank(message = "Message cannot be blank") String> messages;

    private String userId;
    private String sessionId;

    public BulkTransactionRequest(List<String> messages) {
        this.messages = messages;
    }
}
