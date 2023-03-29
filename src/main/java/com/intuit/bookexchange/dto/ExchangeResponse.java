package com.intuit.bookexchange.dto;

import com.intuit.bookexchange.entities.Exchange;
import com.intuit.bookexchange.entities.Product;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@Builder
@Setter
public class ExchangeResponse {
    private int exchangeRequestId;
    private Product exchangeProduct;
    private Exchange.Status status;
}
