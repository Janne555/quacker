package com.group5.quacker.validators;

import com.group5.quacker.constraints.UniqueNameConstraint;
import com.group5.quacker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueNameValidator implements ConstraintValidator<UniqueNameConstraint, String> {
    @Autowired
    UserRepository userRepository;

    @Override
    public void initialize(UniqueNameConstraint name) {
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext cxt) {
        return userRepository.findByName(name) == null;
    }
}
