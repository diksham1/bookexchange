package com.intuit.bookexchange.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "exchanges")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exchange {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int senderUserId;
    private int senderProductId;

    public enum Status {
        EXCHANGE_PENDING,
        EXCHANGE_COMPLETED,
        BOOK_BORROWED
    }

    private Status status;

    @Nullable
    private Integer receiverUserId;

    @Nullable
    private Integer receiverProductId;
}
