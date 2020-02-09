package com.group5.quacker.models;

import com.group5.quacker.constraints.UniqueEmailConstraint;

public class PersonalInfoForm {
    @UniqueEmailConstraint
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
