package edu.agh.zp.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( ElementType.FIELD )
@Retention( RetentionPolicy.RUNTIME )
@Constraint( validatedBy = PeselValidator.class )
public @interface Age {
	String message() default "Musisz być pełnoletni żeby się zarestrować.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}

