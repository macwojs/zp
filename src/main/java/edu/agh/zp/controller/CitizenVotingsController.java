package edu.agh.zp.controller;

import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.*;
import edu.agh.zp.services.CitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping ( value = { "/glosowania" } )
public class CitizenVotingsController {
	@Autowired
	VotingRepository votingRepository;

	@Autowired
	OptionSetRepository optionSetSession;

	@Autowired
	OptionRepository optionSession;

	@Autowired
	SetRepository setSession;

	@Autowired
	VotingTimerRepository votingTimerSession;

	@Autowired
	VotingControlRepository votingControlSession;

	@Autowired
	LogRepository logR;

	@Autowired
	CitizenService cS;


	@GetMapping ( value = { "" } )
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName("Votings/glosowania.html");
		return modelAndView;
	}

	@GetMapping ( value = { "/prezydenckie/plan" } )
	public ModelAndView prezydentForm() {
		deleteOldVotingData( java.sql.Date.valueOf( LocalDate.now( ) ) );
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.addObject( "ErrorList", null );
		modelAndView.setViewName("Votings/presidentVotingAdd.html");
		return modelAndView;
	}

	@PostMapping ( value = { "/prezydenckie/planAdd" } )
	public Object prezydentSubmit( @RequestParam Map< String, String > reqParameters, final HttpServletRequest request ) {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName("Calendar/kalendarz.html");
		String data = reqParameters.remove( "date" );
		SetEntity set = new SetEntity( "Wybory Prezydenckie " + data );
		LocalDate time = timeVerify( data, 7 );
		Optional<CitizenEntity> citizen = cS.findByEmail(request.getRemoteUser());
		VotingEntity voting = null;
		if(citizen.isPresent()) {
			if (time == null || reqParameters.size() < 3 || reqParameters.containsValue("")) {
				ArrayList<String> errors = errorsMsg(time, 7, reqParameters);
				modelAndView.setViewName("Votings/presidentVotingAdd.html");
				modelAndView.addObject("ErrorList", errors);
				logR.save(Log.failedAddVoting("Failed to add new presidential voting", citizen.get()));
				return modelAndView;
			}
			setSession.save(set);
			voting = new VotingEntity(java.sql.Date.valueOf(time), java.sql.Time.valueOf(LocalTime.parse("06:00:00")), java.sql.Time.valueOf(LocalTime.parse("21:00:00")), set, null, VotingEntity.TypeOfVoting.PREZYDENT, "Wybory Prezydenckie " + data);
			VotingEntity check = votingRepository.save(voting);
			if (votingRepository.findById(check.getVotingID()).isPresent()) {
				logR.save(Log.successAddVoting("Add new presidential voting", check, citizen.get()));
			}
			votingTimerSession.save(new VotingTimerEntity(voting.getVotingID(), java.sql.Date.valueOf(time)));
			for (Map.Entry<String, String> entry : reqParameters.entrySet()) {
				if (entry.getKey().equals("_csrf"))
					continue;
				OptionEntity option = new OptionEntity(entry.getValue());
				optionSession.save(option);
				optionSetSession.save(new OptionSetEntity(option, set));
			}
		}

		RedirectView redirect = new RedirectView();
		assert voting != null;
		redirect.setUrl("/wydarzenie/" + voting.getVotingID());
		return redirect;
	}

	@GetMapping ( value = { "/referendum/plan" } )
	public ModelAndView referendumForm() {
		deleteOldVotingData( java.sql.Date.valueOf( LocalDate.now( ) ) );
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.addObject( "ErrorList", null );
		modelAndView.setViewName("Votings/referendumAdd.html");
		return modelAndView;
	}

	@PostMapping ( value = { "/referendum/planAdd" } )
	public Object referendumSubmit( @RequestParam Map< String, String > reqParameters, final HttpServletRequest request ) {
		ModelAndView modelAndView = new ModelAndView( );
		String data = reqParameters.remove( "date" );
		LocalDate time = timeVerify( data, 7 );
		String desc = reqParameters.remove( "desc" );
		Optional<CitizenEntity> citizen = cS.findByEmail(request.getRemoteUser());
		VotingEntity voting = null;
		if(citizen.isPresent()) {
			if ( time == null || desc.isEmpty( ) ) {
				ArrayList< String > errors = new ArrayList<>( );
				if ( time == null )
					errors.add( "Wydarzenie musi być zaplanowane z 7 dniowym wyprzedzeniem\n" );
				if ( desc.isEmpty( ) )
					errors.add( "Należy wpisać treść pytania\n" );
				modelAndView.setViewName("Votings/referendumAdd.html");
				modelAndView.addObject( "ErrorList", errors );
				logR.save(Log.failedAddVoting("Failed to add new referendum - wrong time or question is empty", citizen.get()));
				return modelAndView;
			}
			voting = new VotingEntity( java.sql.Date.valueOf( time ), java.sql.Time.valueOf( LocalTime.parse( "06:00:00" ) ), java.sql.Time.valueOf( LocalTime.parse( "21:00:00" ) ), setSession.findById( 1L ).get( ), null, VotingEntity.TypeOfVoting.REFERENDUM, desc );
			VotingEntity check = votingRepository.save( voting );
			if( votingRepository.findById(check.getVotingID()).isPresent()){
				logR.save(Log.successAddVoting("Add new referendum voting",  check, citizen.get()));
			}
			votingTimerSession.save( new VotingTimerEntity( voting.getVotingID( ), java.sql.Date.valueOf( time ) ) );
		}

		RedirectView redirect = new RedirectView();
		assert voting != null;
		redirect.setUrl("/wydarzenie/" + voting.getVotingID());
		return redirect;
	}

	public LocalDate timeVerify( String time, int delay ) {
		if ( time.isEmpty( ) )
			return null;
		LocalDate now = java.time.LocalDate.now( );
		now = now.plusDays( delay );
		LocalDate res = LocalDate.parse( time );
		if ( now.isAfter( res ) )
			return null;
		return res;
	}

	public ArrayList< String > errorsMsg( LocalDate date, int delay, Map< String, String > param ) {
		ArrayList< String > res = new ArrayList<>( );
		if ( date == null ) {
			res.add( "wydarzenie musi być zaplanowane z " + delay + " dniowym wyprzedzeniem\n" );
		}
		if ( param.size( ) < 3 ) {
			res.add( "musisz podać przynajmnej 2 kandydatów\n" );
		}
		if ( param.containsValue( "" ) ) {
			res.add( "dane kandydata nie mogą być puste\n" );
		}
		return res;
	}

	public void deleteOldVotingData( Date time ) {
		List< VotingTimerEntity > list = votingTimerSession.findByEraseBefore( time );
		if ( list.isEmpty( ) )
			return;
		for ( VotingTimerEntity Timer : list ) {
			votingControlSession.deleteByVotingID( votingRepository.findByVotingID( Timer.getVotingID( ) ) );
			votingTimerSession.delete( Timer );
		}
	}

	@GetMapping ( value = { "/zmianaDaty/{id}" } )
	public Object votingDateChange( @RequestParam ( value = "dateForm", required = false ) Date dateForm, @PathVariable long id, final HttpServletRequest request) {
		VotingEntity voting = votingRepository.findByVotingID( id );
		if (voting == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Voting not found");
		ModelAndView model = new ModelAndView();
		Optional<CitizenEntity> citizen = cS.findByEmail(request.getRemoteUser());
		if(citizen.isPresent()) {
			Time timeSec = java.sql.Time.valueOf(LocalTime.now());
			java.util.Date dateSec = java.sql.Date.valueOf(LocalDate.now());
			boolean ended = (voting.getVotingDate().before(dateSec) || (voting.getVotingDate().equals(dateSec) && voting.getCloseVoting().before(timeSec)));
			boolean during = (voting.getVotingDate().equals(dateSec) && voting.getOpenVoting().before(timeSec) && voting.getCloseVoting().after(timeSec));
			if (ended || during) {
				logR.save(Log.failedEditVoting("Edition of voting failure - Voting is ongoing or has ended", voting, citizen.get()));
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Voting is ongoing or has ended");
			}

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			boolean hasUserRoleAdmin = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
			boolean hasUserRoleSejm = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_MARSZALEK_SEJMU"));
			boolean hasUserRolePresident = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_PREZYDENT"));

			if (voting.getVotingType() == VotingEntity.TypeOfVoting.REFERENDUM)
				if (!(hasUserRoleAdmin || hasUserRoleSejm || hasUserRolePresident)) {
					logR.save(Log.failedEditVoting("Edition of referendum voting failure - forbidden for rule other than admin, Marszalek Sejmu or Prezydent", voting, citizen.get()));
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin or Marszalek Sejmu or Prezydent can change voting date");
				}

			if (voting.getVotingType() == VotingEntity.TypeOfVoting.PREZYDENT)
				if (!(hasUserRoleAdmin || hasUserRoleSejm)) {
					logR.save(Log.failedEditVoting("Edition of presidential voting failure - forbidden for rule other than admin and Marszalek Senatu", voting, citizen.get()));
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin or Marszalek Senatu can change voting date");
				}
			if (voting.getVotingType() != VotingEntity.TypeOfVoting.REFERENDUM && voting.getVotingType() != VotingEntity.TypeOfVoting.PREZYDENT) {
				logR.save(Log.failedEditVoting("Edition of voting failure - wrong voting type", voting, citizen.get()));
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wrong voting type");
			}

			if (voting.getVotingType() == VotingEntity.TypeOfVoting.REFERENDUM) {
				model.addObject("heading", "Zmiana terminu referendum");
				model.addObject("name", "Referendum:" + voting.getVotingDescription());
			}
			if (voting.getVotingType() == VotingEntity.TypeOfVoting.PREZYDENT) {
				model.addObject("heading", "Zmiana terminu wyborów");
				model.addObject("name", "Wybory prezydenckie");
			}

			String error = null;
			if (dateForm != null) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, +7);
				java.util.Date dateNow = cal.getTime();

				if (dateForm.before(dateNow))
					error = "Głosowanie może być najwcześniej za 7 dni";
				model.addObject("error", error);
				logR.save(Log.failedEditVoting("Edition of voting failure - date shorten than 7 days", voting, citizen.get()));
			}

			//Wyslano zadanie zmiany daty, nie ma errora, zmieniamy
			if (error == null && dateForm != null) {
				voting.setVotingDate(dateForm);
				VotingEntity check = votingRepository.save(voting);
				if (votingRepository.findById(check.getVotingID()).isPresent()) {
					logR.save(Log.successEditVoting("Edit voting successfully", check, citizen.get()));
				}
				RedirectView redirect = new RedirectView();
				redirect.setUrl("/wydarzenie/" + id);
				return redirect;
			}
		}

		Date date = voting.getVotingDate( );
		String pattern = "dd MMMMM yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat( pattern, new Locale( "pl", "PL" ) );
		String formattedDate = simpleDateFormat.format( date );

		model.addObject( "currentDate", formattedDate );
		model.addObject( "refName", voting.getVotingDescription( ) );
		model.setViewName( "changeEventDate/changePresRef" );
		return model;
	}

}


