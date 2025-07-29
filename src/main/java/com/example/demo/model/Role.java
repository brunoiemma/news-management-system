package com.example.demo.model;

public enum Role {
    ROLE_ADMIN("Full access to everything"),
    ROLE_PUBLISHER("Can review and publish articles"),
    ROLE_REDACTOR("Can create and edit articles"),
    ROLE_SUBSCRIBER("Can only read published articles");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name();
    }
}
