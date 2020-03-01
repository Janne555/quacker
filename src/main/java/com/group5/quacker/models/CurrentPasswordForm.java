package com.group5.quacker.models;

import com.group5.quacker.constraints.CurrentPasswordConstraint;

public class CurrentPasswordForm {
    @CurrentPasswordConstraint
    private String currentPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
}
