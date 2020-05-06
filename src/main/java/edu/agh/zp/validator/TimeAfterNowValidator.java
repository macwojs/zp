package edu.agh.zp.validator;

import edu.agh.zp.objects.VotingEntity;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class TimeAfterNowValidator implements ConstraintValidator<TimeAfterNow, VotingEntity > {
	@Override
	public boolean isValid( VotingEntity voting, ConstraintValidatorContext constraintValidatorContext ) {
		Date now = new Date();
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date provided = voting.getVotingDate();

		Time providedTime;
		try {
			providedTime = new Time(formatter.parse(voting.getClose()).getTime());
		} catch ( ParseException e ) {
			e.printStackTrace( );
			return false;
		}
		Date dateWithTime = new Date(provided.getYear(), provided.getMonth(), provided.getDay(), providedTime.getHours(), providedTime.getMonth(), providedTime.getSeconds() );

		return dateWithTime.after( now );
	}
}


