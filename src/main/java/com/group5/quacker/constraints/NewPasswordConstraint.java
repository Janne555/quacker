package com.group5.quacker.constraints;

import com.group5.quacker.validators.NewPasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NewPasswordValidator.class)
public @interface NewPasswordConstraint {
    String message() default "Passwords does not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
