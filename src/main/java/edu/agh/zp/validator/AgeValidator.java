package edu.agh.zp.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Date;
import java.time.LocalDate;

public class AgeValidator implements ConstraintValidator< Age, String > {
	@Override
	public boolean isValid( String s, ConstraintValidatorContext constraintValidatorContext ) {

		int p3 = Character.getNumericValue( s.charAt( 2 ) );

		if (p3>1) {
            LocalDate now = LocalDate.now();
            Date nowSql = java.sql.Date.valueOf(now.minusYears(18));
            String str = "20" + s.substring(0, 2) + "-" + (p3 - 2) + s.charAt(3) + "-" + s.substring(4, 6);
            Date birth = java.sql.Date.valueOf(str);

            return birth.before(nowSql) || birth.equals(nowSql);
        }
		return true;
	}
}

