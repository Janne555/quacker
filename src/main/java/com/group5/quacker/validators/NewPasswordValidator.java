package com.group5.quacker.validators;

import com.group5.quacker.constraints.CurrentPasswordConstraint;
import com.group5.quacker.constraints.NewPasswordConstraint;
import com.group5.quacker.constraints.UniqueEmailConstraint;
import com.group5.quacker.models.PasswordForm;
import com.group5.quacker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NewPasswordValidator implements ConstraintValidator<NewPasswordConstraint, PasswordForm>
{
    @Autowired
    UserRepository userRepository;

    @Override
    public void initialize(NewPasswordConstraint temp) {
    }

    @Override
    public boolean isValid(PasswordForm form, ConstraintValidatorContext cxt) {
        System.out.println(form.getNew_password() + form.getConfirm_new_password());
        System.out.println(form.getNew_password().equals(form.getConfirm_new_password()));

        return form.getNew_password().equals(form.getConfirm_new_password());
    }
}
