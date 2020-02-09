package com.group5.quacker.validators;

import com.group5.quacker.constraints.UniqueEmailConstraint;
import com.group5.quacker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmailConstraint, String> {
    @Autowired
    UserRepository userRepository;

    @Override
    public void initialize(UniqueEmailConstraint name) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext cxt) {
        return userRepository.findByEmailIs(email) == null;
    }
}
