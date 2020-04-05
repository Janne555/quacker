package com.group5.quacker.enums;

public enum FeedbackCategory {
    BUG("Bug"),
    USABILITY("Usability"),
    OTHER("Other");

    private String defaultValue;

    private FeedbackCategory(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String displayValue() {
        return this.defaultValue;
    }
}
