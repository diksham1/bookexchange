package com.intuit.bookexchange.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private int userId;
    private String name;
    private int rewardPoints;
    private double rating;
    
    public User(String name, int rewardPoints, double rating) {
        this.name = name;
        this.rewardPoints = rewardPoints;
        this.rating = rating;
    }
}
