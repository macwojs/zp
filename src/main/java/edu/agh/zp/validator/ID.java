package edu.agh.zp.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( ElementType.FIELD )
@Retention( RetentionPolicy.RUNTIME )
@Constraint( validatedBy = IDValidator.class )
public @interface ID {
	String message() default "Wprowadź poprawny numer dowodu osobistego.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}

