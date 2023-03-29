package com.intuit.bookexchange.dto;

import com.intuit.bookexchange.entities.Product;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
@Builder
public class InitiateExchangeResponse {
    private int exchangeRequestId;
    private Product exchangeProduct;
}
