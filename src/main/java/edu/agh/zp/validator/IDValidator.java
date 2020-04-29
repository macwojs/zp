package edu.agh.zp.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IDValidator implements ConstraintValidator< ID, String > {
	@Override
	public boolean isValid( String s, ConstraintValidatorContext constraintValidatorContext ) {

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
		return ( d1 * 7 + d2 * 3 + d3 * 1 + d4 * 9 + d5 * 7 + d6 * 3 + d7 * 1 + d8 * 7 + d9 * 3 ) % 10 == 0;
	}

	private int getNumber( char a ) {
		//poszczegolnym litera przypisuje sie wartosci liczbowe
		switch ( a ) {
			case 'A':
				return 10;
			case 'B':
				return 11;
			case 'C':
				return 12;
			case 'D':
				return 13;
			case 'E':
				return 14;
			case 'F':
				return 15;
			case 'G':
				return 16;
			case 'H':
				return 17;
			case 'I':
				return 18;
			case 'J':
				return 19;
			case 'K':
				return 20;
			case 'L':
				return 21;
			case 'M':
				return 22;
			case 'N':
				return 23;
			case 'O':
				return 24;
			case 'P':
				return 25;
			case 'Q':
				return 26;
			case 'R':
				return 27;
			case 'S':
				return 28;
			case 'T':
				return 29;
			case 'U':
				return 30;
			case 'V':
				return 31;
			case 'W':
				return 32;
			case 'X':
				return 33;
			case 'Y':
				return 34;
			case 'Z':
				return 35;
			default:
				return 0;
		}
	}
}

