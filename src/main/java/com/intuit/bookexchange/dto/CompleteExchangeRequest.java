package com.intuit.bookexchange.dto;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class CompleteExchangeRequest {
    private int acceptorExchangeRequestId;
}
