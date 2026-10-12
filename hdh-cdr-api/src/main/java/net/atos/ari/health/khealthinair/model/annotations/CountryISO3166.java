package net.atos.ari.health.khealthinair.model.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import net.atos.ari.health.khealthinair.model.validators.CountryValidator;

@Target(java.lang.annotation.ElementType.FIELD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CountryValidator.class)
@Documented
public @interface CountryISO3166 {

  String message() default "{Country.invalid}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
