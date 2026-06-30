package com.foodecommerce.foodies.io;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SepayWebhookRequest(
        @JsonProperty("id")          Long id,
        @JsonProperty("gateway")     String gateway,
        @JsonProperty("transactionDate") String transactionDate,
        @JsonProperty("accountNumber")   String accountNumber,
        @JsonProperty("code")            String code,          // paymentCode "DN..."
        @JsonProperty("content")         String content,
        @JsonProperty("transferType")     String transferType,
        @JsonProperty("transferAmount")   long transferAmount,
        @JsonProperty("accumulated")      long accumulated,
        @JsonProperty("subAccount")       String subAccount,
        @JsonProperty("referenceCode")    String referenceCode,
        @JsonProperty("description")      String description
) {
}
