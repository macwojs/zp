package edu.agh.zp.controller;

import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.*;
import edu.agh.zp.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping ( value = { "/parlament" } )
public class ParlamentController {

	@Autowired
	private StorageService storageService;

	@Autowired
	private OptionRepository optionRepository;

	@Autowired
	private CitizenRepository citizenRepository;

	@Autowired
	private DocumentTypeRepository documentTypeRepository;

	@Autowired
	private DocumentStatusRepository documentStatusRepository;

	@Autowired
	private VoteRepository voteRepository;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private VotingRepository votingRepository;


	@GetMapping ( value = { "" } )
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "parlament" );
		return modelAndView;
	}


	@GetMapping ( value = { "/ustawy" } )
	public ModelAndView ustawy() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "ustawy" );
		return modelAndView;
	}

	@GetMapping ( value = { "/documentForm" } )
	public ModelAndView documentForm( ModelAndView model ) {
		List< DocumentTypeEntity > types = documentTypeRepository.findAll( );
		model.addObject( "types", types );

		List< DocumentStatusEntity > statuses = documentStatusRepository.findAll( );
		model.addObject( "statuses", statuses );

		model.addObject( "document", new DocumentEntity( ) );

		model.setViewName( "documentForm" );
		return model;
	}

	@PostMapping ( value = { "/documentForm" }, consumes = { "multipart/form-data" } )
	public ModelAndView documentFormSubmit( @RequestParam ( "file" ) MultipartFile file, RedirectAttributes redirectAttributes, @Valid @ModelAttribute ( "document" ) DocumentEntity document, BindingResult res ) {
		if ( res.hasErrors( ) ) {
			return new ModelAndView( "documentForm" );
		}

		if ( !file.isEmpty( ) ) {
			String path = storageService.uploadFile( file );
			document.setPdfFilePath( path );
		}

		documentRepository.save( document );

		RedirectView redirect = new RedirectView( );
		redirect.setUrl( "/parlament" );
		return new ModelAndView( redirect );
	}

	@GetMapping ( value = { "/vote/{id}" } )
	public ModelAndView parlamentVote( ModelAndView model, @PathVariable long id, Principal principal ) {
		VotingEntity voting = votingRepository.findByVotingID( id );

		Optional< CitizenEntity > optCurUser = citizenRepository.findByEmail( principal.getName( ) );
		Optional< VoteEntity > vote = voteRepository.findByCitizenID_CitizenIDAndVotingID_VotingID( id, optCurUser.get( ).getCitizenID( ) );
		if ( vote.isPresent( ) ) {
			if ( voting.getVotingType( ).equals( VotingEntity.TypeOfVoting.SEJM ) )
				model.addObject( "th_redirect", "/parlament/sejm" );
			else
				model.addObject( "th_redirect", "/parlament/senat" );
			model.setViewName( "418_REPEAT_VOTE" );
			return model;
		}

		Authentication auth = SecurityContextHolder.getContext( ).getAuthentication( );
		model.addObject( "voting", voting );
		model.addObject( "id", id );
//	    for (OptionSetEntity optionSet :optionSetRepository.findBySetIDcolumn(setRepository.findById(1L).get()))
//		{
//			TODO Nie usuwać, ta idea będzie użyta w wyborach  prezydenckich!
//		}
		model.setViewName( "parliamentVoting" );
		return model;
	}

	@PostMapping ( value = { "/vote/{id}" } )
	public ModelAndView parlamentVoteSubmit( @PathVariable long id, @RequestParam ( "votingRadio" ) long radio ) {
		VoteEntity vote = new VoteEntity( );
		vote.setOptionID( optionRepository.findByOptionID( radio ).get( ) );
		Authentication auth = SecurityContextHolder.getContext( ).getAuthentication( );
		vote.setCitizenID( citizenRepository.findByEmail( auth.getName( ) ).get( ) );
		vote.setVotingID( votingRepository.findByVotingID( id ) );
		LocalTime time = LocalTime.now( );
		LocalDate date = LocalDate.now( );
		VotingEntity voting = vote.getVotingID( );
		if ( voting.getCloseVoting( ).before( java.sql.Time.valueOf( time ) ) || !voting.getVotingDate( ).equals( java.sql.Date.valueOf( date ) ) ) {
			ModelAndView model = new ModelAndView( );
			model.setViewName( "timeOut" );
			if ( voting.getVotingType( ).equals( VotingEntity.TypeOfVoting.SEJM ) )
				model.addObject( "type", "/parlament/sejm" );
			else
				model.addObject( "type", "/parlament/senat" );
			return model;
		}
		vote.setVoteTimestamp( new Timestamp( System.currentTimeMillis( ) ) );
		voteRepository.save( vote );
		RedirectView redirect = new RedirectView( );
		if ( voting.getVotingType( ).equals( VotingEntity.TypeOfVoting.SEJM ) )
			redirect.setUrl( "/parlament/sejm" );
		else
			redirect.setUrl( "/parlament/senat" );
		return new ModelAndView( redirect );
	}

}


