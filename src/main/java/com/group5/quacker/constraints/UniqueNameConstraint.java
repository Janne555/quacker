package com.group5.quacker.constraints;

import com.group5.quacker.validators.UniqueNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * A constraint interface for checking name uniqueness
 * To be used as a decorator for validated fields
 */
@Documented
@Constraint(validatedBy = UniqueNameValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueNameConstraint {
    String message() default "Name must be unique";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
