package edu.agh.zp.controller;

import edu.agh.zp.objects.DocumentEntity;
import edu.agh.zp.objects.SetEntity;
import edu.agh.zp.objects.VotingEntity;
import edu.agh.zp.objects.createVotingList;
import edu.agh.zp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping ( value = { "/parlament/sejm" } )
public class SejmController {

	@Autowired
	private SetRepository setRepository;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private CitizenRepository citizenRepository;

	@Autowired
	private VotingRepository votingRepository;

	@Autowired
	private VoteRepository voteRepository;

	@GetMapping ( value = { "" } )
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "sejm" );
		createVotingList.run( modelAndView, VotingEntity.TypeOfVoting.SEJM, votingRepository );
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
	public ModelAndView documentFormSubmit( @Valid @ModelAttribute ( "voting" ) VotingEntity voting, BindingResult res ) throws ParseException {
		if ( res.hasErrors( ) ) {
			for ( Object i : res.getAllErrors( ) ) {
				System.out.print( "\n" + i.toString( ) + "\n" );
			}

			//Musi być ponownie dodane, bo inaczej nie wypełnia listy
			ModelAndView model = new ModelAndView( );
			List< DocumentEntity > documents = documentRepository.findByDocForSejm( );
			model.addObject( "documents", documents );
			model.setViewName( "sejmVotingAdd" );
			return model;
		}
		Optional< SetEntity > set = setRepository.findById( (long) 1 );
		if ( set.isPresent( ) ) {
			voting.setSetID_column( set.get( ) );
			DateFormat formatter = new SimpleDateFormat( "HH:mm:ss" );

			Time timeValueOpen = new Time( formatter.parse( voting.getOpen( ) ).getTime( ) );
			Time timeValueClose = new Time( formatter.parse( voting.getClose( ) ).getTime( ) );
			voting.setCloseVoting( timeValueClose );
			voting.setOpenVoting( timeValueOpen );

			voting.setVotingType( VotingEntity.TypeOfVoting.SEJM );
			votingRepository.save( voting );
		}
		RedirectView redirect = new RedirectView( );
		redirect.setUrl( "/parlament/sejm" );
		return new ModelAndView( redirect );
	}
}
