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
@RequestMapping ( value = { "/parlament/senat" } )
public class SenatController {

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
		createVotingList.run( modelAndView, Collections.singletonList( VotingEntity.TypeOfVoting.SENAT ), votingRepository );
		modelAndView.setViewName("Votings/senat.html");
		return modelAndView;
	}

	@GetMapping ( value = { "/voteAdd" } )
	public ModelAndView sejmVoteAdd( ModelAndView model ) {
		List< DocumentEntity > documents = documentRepository.findByDocForSenat( );
		Optional< SetEntity > set = setRepository.findById( (long) 1 );
		if ( set.isPresent( ) ) {
			model.addObject( "documents", documents );
			model.addObject( "voting", new VotingEntity( ) );
		}
		model.setViewName("Votings/senatVotingAdd.html");
		return model;
	}

	@PostMapping ( value = { "/voteAdd" } )
	public ModelAndView documentFormSubmit( @Valid @ModelAttribute ( "voting" ) VotingEntity voting, BindingResult res, final HttpServletRequest request ) throws Exception {
		CitizenEntity citizen = cS.findByEmail(request.getRemoteUser()).orElseThrow(()-> new Exception("Citizen not found"));
		if ( res.hasErrors( ) ) {
			for ( Object i : res.getAllErrors( ) ) {
				System.out.print( "\n" + i.toString( ) + "\n" );
			}

			//Musi być ponownie dodane, bo inaczej nie wypełnia listy
			ModelAndView model = new ModelAndView( );
			List< DocumentEntity > documents = documentRepository.findByDocForSenat( );
			model.addObject( "documents", documents );
			model.setViewName("Votings/senatVotingAdd.html");
			logR.save(Log.failedAddVoting("Failed to Add Sejm voting - validation problems", citizen));
			return model;
		}
		Optional< SetEntity > set = setRepository.findById( (long) 2 );
		if ( set.isPresent( ) ) {
			voting.setSetID_column( set.get( ) );
			DateFormat formatter = new SimpleDateFormat( "HH:mm:ss" );

			Time timeValueOpen = new Time( formatter.parse( voting.getOpen( ) ).getTime( ) );
			Time timeValueClose = new Time( formatter.parse( voting.getClose( ) ).getTime( ) );
			voting.setCloseVoting( timeValueClose );
			voting.setOpenVoting( timeValueOpen );

			voting.setVotingType( VotingEntity.TypeOfVoting.SENAT );
			VotingEntity check = votingRepository.save( voting );
			logR.save( Log.successAddVoting("Add Senat voting", voting, citizen));
		}
		RedirectView redirect = new RedirectView( );
		redirect.setUrl( "/parlament/senat" );
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
		List< Long > statusID = Collections.singletonList( 2L );
		List< VotingEntity > votings = votingRepository.findByVotingDateAndDocumentIDDocStatusIDDocStatusIDIn( dateSQL, statusID );
		String pattern = "dd MMMMM yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat( pattern, new Locale( "pl", "PL" ) );
		System.out.println( date );
		String formattedDate = simpleDateFormat.format( date );
		model.addObject( "schedule_name", "Senacie" );
		model.addObject( "current_date", formattedDate );
		model.addObject( "votings", votings );
		model.setViewName("Votings/votingSchedule.html");
		return model;
	}
}
