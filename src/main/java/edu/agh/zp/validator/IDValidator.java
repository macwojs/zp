package edu.agh.zp.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IDValidator implements ConstraintValidator< ID, String > {
	@Override
	public boolean isValid( String s, ConstraintValidatorContext constraintValidatorContext ) {

		if(s.length()<9)
			return false;
		int d1 = getNumber( s.charAt( 0 ) );
		int d2 = getNumber( s.charAt( 1 ) );
		int d3 = getNumber( s.charAt( 2 ) );
		int d4 = Character.getNumericValue( s.charAt( 3 ) );
		int d5 = Character.getNumericValue( s.charAt( 4 ) );
		int d6 = Character.getNumericValue( s.charAt( 5 ) );
		int d7 = Character.getNumericValue( s.charAt( 6 ) );
		int d8 = Character.getNumericValue( s.charAt( 7 ) );
		int d9 = Character.getNumericValue( s.charAt( 8 ) );

		//suma znakow pomnozona przez wagi musi byc podzielna przez 10 zeby numer byl poprawny
		return ( d1 * 7 + d2 * 3 + d3 + d4 * 9 + d5 * 7 + d6 * 3 + d7 + d8 * 7 + d9 * 3 ) % 10 == 0;
	}

	private int getNumber( char a ) {
		int res =  (int) a - 55;
		if (res<10 || res>35) return 0;
		return res;
	}

}

