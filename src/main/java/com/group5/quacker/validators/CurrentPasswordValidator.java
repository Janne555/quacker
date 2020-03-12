package com.group5.quacker.validators;

import com.group5.quacker.constraints.CurrentPasswordConstraint;
import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CurrentPasswordValidator implements ConstraintValidator<CurrentPasswordConstraint, String> {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void initialize(CurrentPasswordConstraint temp) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext cxt) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByName(auth.getName());
        if (user == null) {
            return false;
        } else {
            return passwordEncoder.matches(password, user.getPasswordHash());
        }
    }
}
