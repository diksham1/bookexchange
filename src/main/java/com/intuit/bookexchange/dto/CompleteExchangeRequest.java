package com.intuit.bookexchange.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@Builder
public class CompleteExchangeRequest {
    private int acceptorExchangeRequestId;
}
