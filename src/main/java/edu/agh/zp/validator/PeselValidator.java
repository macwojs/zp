package edu.agh.zp.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PeselValidator implements ConstraintValidator< Pesel, String > {
	@Override
	public boolean isValid( String s, ConstraintValidatorContext constraintValidatorContext ) {

		int p1 = Character.getNumericValue( s.charAt( 0 ) );
		int p2 = Character.getNumericValue( s.charAt( 1 ) );
		int p3 = Character.getNumericValue( s.charAt( 2 ) );
		int p4 = Character.getNumericValue( s.charAt( 3 ) );
		int p5 = Character.getNumericValue( s.charAt( 4 ) );
		int p6 = Character.getNumericValue( s.charAt( 5 ) );
		int p7 = Character.getNumericValue( s.charAt( 6 ) );
		int p8 = Character.getNumericValue( s.charAt( 7 ) );
		int p9 = Character.getNumericValue( s.charAt( 8 ) );
		int p10 = Character.getNumericValue( s.charAt( 9 ) );
		int p11 = Character.getNumericValue( s.charAt( 10 ) );

		return ( 9 * p1 + 7 * p2 + 3 * p3 + 1 * p4 + 9 * p5 + 7 * p6 + 3 * p7 + 1 * p8 + 9 * p9 + 7 * p10 ) == p11;
	}
}

