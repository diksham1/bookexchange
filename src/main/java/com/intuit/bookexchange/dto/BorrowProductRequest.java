package com.intuit.bookexchange.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BorrowProductRequest {
    private int borrowerUserId;
}
