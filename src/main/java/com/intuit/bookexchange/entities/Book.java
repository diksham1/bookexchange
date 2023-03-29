package com.intuit.bookexchange.entities;

import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity(name = "books")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class Book extends Product {
    private String name;
    private String authorName;
    private double rating;
}
