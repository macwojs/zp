package edu.agh.zp.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target ( { TYPE, ANNOTATION_TYPE } )
@Retention ( RUNTIME )
@Constraint ( validatedBy = PasswordValidator.class )
public @interface Password {
	String message() default "Hasła muszą być takie same.";

	Class< ? >[] groups() default {};

	Class< ? extends Payload >[] payload() default {};
}

