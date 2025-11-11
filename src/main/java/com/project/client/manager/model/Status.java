package com.project.client.manager.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {
    PAID("paid"),
    OVERDUE("overdue"),
    DRAFT("draft"),
    SENT("sent");

    public final String label;

    Status(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return this.label;
    }

    @JsonCreator
    public static Status fromLabel(String label) {
        if(label ==null)return null;
        String trimmedLabel = label.trim();
        for(Status s: values()){
            if(s.label.equalsIgnoreCase(trimmedLabel))return s;
        }

        return null;
    }
}
