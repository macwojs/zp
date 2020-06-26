package edu.agh.zp.controller;

import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.DocumentRepository;
import edu.agh.zp.repositories.LogRepository;
import edu.agh.zp.repositories.SetRepository;
import edu.agh.zp.repositories.VotingRepository;
import edu.agh.zp.services.CitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping ( value = { "/parlament/sejm" } )
public class SejmController {

	@Autowired
	private SetRepository setRepository;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private VotingRepository votingRepository;

	@Autowired
	private LogRepository logR;

	@Autowired
	private CitizenService cS;


	@GetMapping ( value = { "" } )
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "sejm" );
		createVotingList.run( modelAndView, Collections.singletonList( VotingEntity.TypeOfVoting.SEJM ), votingRepository );
		return modelAndView;
	}

	@GetMapping ( value = { "/voteAdd" } )
	public ModelAndView sejmVoteAdd( ModelAndView model ) {
		List< DocumentEntity > documents = documentRepository.findByDocForSejm( );
		Optional< SetEntity > set = setRepository.findById( (long) 1 );
		if ( set.isPresent( ) ) {
			model.addObject( "documents", documents );
			model.addObject( "voting", new VotingEntity( ) );
		}
		model.setViewName( "sejmVotingAdd" );
		return model;
	}

	@PostMapping ( value = { "/voteAdd" } )
	public ModelAndView documentFormSubmit( @Valid @ModelAttribute ( "voting" ) VotingEntity voting, BindingResult res,final HttpServletRequest request ) throws Exception {
		CitizenEntity citizen = cS.findByEmail(request.getRemoteUser()).orElseThrow(()-> new Exception("Citizen not found"));
		if ( res.hasErrors( ) ) {
			for ( Object i : res.getAllErrors( ) ) {
				System.out.print( "\n" + i.toString( ) + "\n" );
			}

			//Musi być ponownie dodane, bo inaczej nie wypełnia listy
			ModelAndView model = new ModelAndView( );
			List< DocumentEntity > documents = documentRepository.findByDocForSejm( );
			model.addObject( "documents", documents );
			model.setViewName( "sejmVotingAdd" );
			logR.save(Log.failedAddVoting("Failed to Add Sejm voting - validation", citizen));
			return model;
		}
		Optional< SetEntity > set = setRepository.findById( (long) 2 );
		if ( set.isPresent( ) ) {
			voting.setSetID_column( set.orElseThrow(() -> new NoSuchElementException("Set Not Found") ) );
			DateFormat formatter = new SimpleDateFormat( "HH:mm:ss" );

			Time timeValueOpen = new Time( formatter.parse( voting.getOpen( ) ).getTime( ) );
			Time timeValueClose = new Time( formatter.parse( voting.getClose( ) ).getTime( ) );
			voting.setCloseVoting( timeValueClose );
			voting.setOpenVoting( timeValueOpen );

			voting.setVotingType( VotingEntity.TypeOfVoting.SEJM );
			votingRepository.save( voting );
			logR.save( Log.successAddVoting("Add Sejm voting", voting, citizen));
		}
		RedirectView redirect = new RedirectView( );
		redirect.setUrl( "/parlament/sejm" );
		return new ModelAndView( redirect );
	}

	@GetMapping ( value = { "/votingSchedule" } )
	public ModelAndView votingSchedule( HttpServletRequest request, @RequestParam ( value = "dateForm", required = false ) Date dateForm ) {
		ModelAndView model = new ModelAndView( );
		java.util.Date date = new java.util.Date( );
		if ( dateForm != null ) {
			date = dateForm;
		}
		Date dateSQL = new Date( date.getTime( ) );
		List< Long > statusID = Arrays.asList( 1L, 5L, 6L, 8L, 9L );
		List< VotingEntity > votings = votingRepository.findByVotingDateAndDocumentIDDocStatusIDDocStatusIDIn( dateSQL, statusID );
		String pattern = "dd MMMMM yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat( pattern, new Locale( "pl", "PL" ) );
		System.out.println( date );
		String formattedDate = simpleDateFormat.format( date );
		model.addObject( "schedule_name", "Sejmie" );
		model.addObject( "current_date", formattedDate );
		model.addObject( "votings", votings );
		model.setViewName( "votingSchedule" );
		return model;
	}

}
