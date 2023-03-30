package com.intuit.bookexchange.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class InitiateExchangeRequest {
    private int ownerUserId;
    private String bookName;
    private String authorName;
}
