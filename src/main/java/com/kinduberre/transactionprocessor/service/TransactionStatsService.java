package com.kinduberre.transactionprocessor.service;

import com.kinduberre.transactionprocessor.dto.ProcessingStats;
import com.kinduberre.transactionprocessor.dto.TransactionData;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TransactionStatsService {
    public ProcessingStats calculateStats(List<TransactionData> transactions) {
        ProcessingStats stats = new ProcessingStats();

        // Calculate transaction type distribution
        Map<String, Integer> typeCount = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getType() != null ? t.getType() : "unknown",
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
        stats.setTransactionTypes(typeCount);

        // Calculate totals
        BigDecimal totalSent = transactions.stream()
                .filter(t -> "send".equals(t.getType()) || "payment".equals(t.getType()))
                .map(t -> t.getAmount() != null ? t.getAmount().getValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalSent(totalSent);

        BigDecimal totalReceived = transactions.stream()
                .filter(t -> "receive".equals(t.getType()) || "transfer".equals(t.getType()))
                .map(t -> t.getAmount() != null ? t.getAmount().getValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalReceived(totalReceived);

        BigDecimal totalFees = transactions.stream()
                .map(t -> t.getTransactionCost() != null ? t.getTransactionCost().getValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalFees(totalFees);

        // Calculate date range
        List<String> dates = transactions.stream()
                .map(TransactionData::getTransactionDate)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        if (!dates.isEmpty()) {
            String dateRange = dates.size() == 1 ? dates.get(0) : dates.get(0) + " to " + dates.get(dates.size() - 1);
            stats.setDateRange(dateRange);
        }

        return stats;
    }
}
