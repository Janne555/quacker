package com.group5.quacker.models;

import com.group5.quacker.validators.UniqueNameConstraint;

public class PersonalInfoForm {
    @UniqueNameConstraint
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
