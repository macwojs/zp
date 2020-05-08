package edu.agh.zp.validator;

import edu.agh.zp.objects.VotingEntity;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class TimeAfterNowValidator implements ConstraintValidator<TimeAfterNow, VotingEntity > {
	@Override
	public boolean isValid( VotingEntity voting, ConstraintValidatorContext constraintValidatorContext ) {
		if (voting.getVotingType()== VotingEntity.TypeOfVoting.PREZYDENT) return true;
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime provided = null;
		try {
			provided = LocalDateTime.parse( voting.getVotingDate() + "T" + new Time(formatter.parse(voting.getClose()).getTime()));
		} catch ( ParseException e ) {
			e.printStackTrace( );
			return false;
		}
		return provided.isAfter( now );
	}

	public LocalDateTime dateAndTimeToLocalDateTime( Date date, Time time) {
		String myDate = date + "T" + time;
		return LocalDateTime.parse(myDate);
	}
}


