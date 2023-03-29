package com.intuit.bookexchange.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class BorrowProductRequest {
    private int borrowerUserId;
}
