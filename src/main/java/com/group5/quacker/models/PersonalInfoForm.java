package com.group5.quacker.models;

import com.group5.quacker.constraints.UniqueEmailConstraint;

/**
 * A model used to transfer information between a form and the system
 */
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
