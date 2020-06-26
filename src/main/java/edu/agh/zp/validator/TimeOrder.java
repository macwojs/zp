package edu.agh.zp.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target ( { TYPE, ANNOTATION_TYPE } )
@Retention ( RUNTIME )
@Constraint ( validatedBy = TimeOrderValidator.class )
public @interface TimeOrder {
	String message() default "Czas zakończenia musi być późniejszy niż rozpoczęcia";

	Class< ? >[] groups() default {};

	Class< ? extends Payload >[] payload() default {};
}

