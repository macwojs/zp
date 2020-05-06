package edu.agh.zp.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;

public class DateAfterNowValidator implements ConstraintValidator<DateAfterNow, Date > {
	@Override
	public boolean isValid( Date date, ConstraintValidatorContext constraintValidatorContext ) {
		Date today = new Date();

		//Brzydkie, ale bez dodatkowej biblioteki chyba sie nie da inaczej
		//I nie bedzie działać przy zmianie czasu, ale to chyba nie tak ważne
		Date now = new Date(today.getTime() - (1000 * 60 * 60 * 24));
		return date.after( now );
	}
}

