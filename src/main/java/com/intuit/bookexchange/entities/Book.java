package com.intuit.bookexchange.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Book extends ExchangeProduct{
    private String name;
    private String authorName;
    private double rating;
}
