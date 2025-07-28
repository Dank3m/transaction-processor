package com.kinduberre.transactionprocessor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionData {
    @JsonProperty("transaction_id")
    private String transactionId;

    private String status;
    private String type;

    @JsonProperty("amount")
    private AmountData amount;

    @JsonProperty("transaction_date")
    private String transactionDate;

    @JsonProperty("transaction_time")
    private String transactionTime;

    @JsonProperty("mpesa_balance")
    private AmountData mpesaBalance;

    @JsonProperty("transaction_cost")
    private AmountData transactionCost;

    @JsonProperty("daily_transaction_limit")
    private DailyLimitData dailyTransactionLimit;

    private Object recipient;
    private Object sender;
    private String merchant;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class AmountData {
        private BigDecimal value;
        private String currency;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class DailyLimitData {
        private BigDecimal remaining;
        private String currency;
    }
}
