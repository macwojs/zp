package edu.agh.zp.controller;

import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.*;
import edu.agh.zp.services.CitizenService;
import edu.agh.zp.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping ( value = { "/parlament" } )
public class ParlamentController {

	@Autowired
	private StorageService storageService;

	@Autowired
	private SetRepository setRepository;

	@Autowired
	private DocumentTypeRepository documentTypeRepository;

	@Autowired
	private DocumentStatusRepository documentStatusRepository;

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

	@GetMapping ( value = { "/sejm/voteAdd" } )
	public ModelAndView sejmVoteAdd( ModelAndView model ) {
		List< DocumentEntity > documents = documentRepository.findByDocTypeID();
		Optional< SetEntity > set = setRepository.findById((long)1);
		if(set.isPresent()) {
			model.addObject("documents", documents);
			model.addObject("voting", new VotingEntity());
		}
		model.setViewName( "parliamentVotingAdd" );
		return model;
	}

	@PostMapping ( value = { "/sejm/voteAdd" } )
	public ModelAndView documentFormSubmit( @Valid @ModelAttribute ( "voting" ) VotingEntity voting, BindingResult res ) throws ParseException {
		if ( res.hasErrors( ) ) {
			for( Object i : res.getAllErrors()){
				System.out.print("\n"+i.toString()+"\n");
			}
			return new ModelAndView( "parliamentVotingAdd" );
		}
		Optional< SetEntity > set = setRepository.findById( (long)1 );
		if(set.isPresent()) {
			voting.setSetID_column(set.get());
			DateFormat formatter = new SimpleDateFormat("HH:mm:ss");

			Time timeValueOpen = new Time(formatter.parse(voting.getOpen()).getTime());
			Time timeValueClose = new Time(formatter.parse(voting.getClose()).getTime());
			voting.setCloseVoting(timeValueClose);
			voting.setOpenVoting(timeValueOpen);

			voting.setVotingType(VotingEntity.TypeOfVoting.SEJM);
			votingRepository.save(voting);
		}
		RedirectView redirect = new RedirectView( );
		redirect.setUrl( "/parlament" );
		return new ModelAndView( redirect );
	}
}
