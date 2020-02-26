package com.group5.quacker.validators;

import com.group5.quacker.constraints.CurrentPasswordConstraint;
import com.group5.quacker.constraints.UniqueEmailConstraint;
import com.group5.quacker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CurrentPasswordValidator implements ConstraintValidator<CurrentPasswordConstraint, String>
{
    @Autowired
    UserRepository userRepository;

    @Override
    public void initialize(CurrentPasswordConstraint temp) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext cxt) {

        return true;
    }
}
