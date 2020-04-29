package edu.agh.zp.validator;

import edu.agh.zp.objects.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, User > {
	@Override
	public boolean isValid( User user, ConstraintValidatorContext constraintValidatorContext ) {

		return user.getPassword()==user.getRepeat_password();
	}
}
