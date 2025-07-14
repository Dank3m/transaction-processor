package com.kinduberre.transactionprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProcessingError {
    private String message;
    private String originalMessage;
    private String errorType;
    private String errorCode;
}
