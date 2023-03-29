package com.intuit.bookexchange.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class InitiateExchangeRequest {
    private int ownerUserId;
    private String bookName;
    private String authorName;
}
