package com.group5.quacker.constraints;

import com.group5.quacker.repositories.UserRepository;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentPasswordConstraint {
    String message() default "Current password does not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
