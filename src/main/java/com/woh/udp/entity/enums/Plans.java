package com.woh.udp.entity.enums;

public enum Plans {
    FREE("Free Plan", 0.0, 1),
    BASIC("Basic Plan", 9.99, 5),
    PREMIUM("Premium Plan", 19.99, 20),
    ENTERPRISE("Enterprise Plan", 49.99, 100);

    private String description;
    private double price;
    private int maxUsers;

    Plans(String description, double price, int maxUsers) {
        this.description = description;
        this.price = price;
        this.maxUsers = maxUsers;
    }
}
