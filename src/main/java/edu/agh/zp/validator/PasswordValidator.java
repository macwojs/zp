package edu.agh.zp.validator;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, CitizenEntity > {
	@Override
	public boolean isValid(  CitizenEntity citizen, ConstraintValidatorContext constraintValidatorContext ) {
		return citizen.getPassword( ).equals( citizen.getRepeatPassword( ) );
	}
}
