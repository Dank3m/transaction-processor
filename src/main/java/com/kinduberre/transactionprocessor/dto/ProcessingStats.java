package com.kinduberre.transactionprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProcessingStats {
    private Map<String, Integer> transactionTypes;
    private BigDecimal totalSent;
    private BigDecimal totalReceived;
    private BigDecimal totalFees;
    private String dateRange;
}
