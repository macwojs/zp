package edu.agh.zp.controller;

import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.*;
import edu.agh.zp.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ParliamentarianRepository parliamentarianRepository;

	@Autowired
	private LogRepository logR;

	@GetMapping ( value = { "" } )
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "parlament" );
		return modelAndView;
	}

	@GetMapping ( value = { "/documentForm" } )
	public ModelAndView documentForm( ModelAndView model ) {
		List< DocumentTypeEntity > types = documentTypeRepository.findAllByDocTypeIDNotIn(Arrays.asList(6L,7L));
		model.addObject( "types", types );

		List< DocumentStatusEntity > statuses = documentStatusRepository.findAll();
		model.addObject( "statuses", statuses );

		model.addObject( "document", new DocumentEntity( ) );

		model.setViewName( "documentForm" );
		return model;
	}

	@PostMapping ( value = { "/documentForm" }, consumes = { "multipart/form-data" } )
	public ModelAndView documentFormSubmit( @RequestParam ( "file" ) MultipartFile file, RedirectAttributes redirectAttributes, @Valid @ModelAttribute ( "document" ) DocumentEntity document, BindingResult res, final HttpServletRequest request ) {
		Optional<CitizenEntity> citizen = citizenRepository.findByEmail(request.getRemoteUser());
		if(citizen.isPresent()) {
			if (res.hasErrors()) {
				logR.save(new Log(Log.Operation.ADD, "Failed to add document", Log.ElementType.DOCUMENT, citizen.get(), Log.Status.FAILURE));
				return new ModelAndView("documentForm");
			}

			if (!file.isEmpty()) {
				String path = storageService.uploadFile(file);
				document.setPdfFilePath(path);
			}
			Date now = Date.valueOf(LocalDate.now());
			document.setDeclaredDate(now);
			document.setLastEdit(now);
			DocumentEntity check = documentRepository.save(document);
			if(documentRepository.findByDocID(check.getDocID()).isPresent()) {
				logR.save(new Log(Log.Operation.ADD, "Add document successfully", Log.ElementType.DOCUMENT, citizen.get(), Log.Status.SUCCESS));
			}
		}
		RedirectView redirect = new RedirectView( );
		redirect.setUrl( "/ustawy/"+document.getDocID() );
		return new ModelAndView( redirect );
	}

	@GetMapping ( value = { "/vote/{id}" } )
	public ModelAndView parlamentVote( ModelAndView model, @PathVariable long id, Principal principal ) {
		VotingEntity voting = votingRepository.findByVotingID( id );

		Optional< CitizenEntity > optCurUser = citizenRepository.findByEmail( principal.getName( ) );
		if (optCurUser.isEmpty())
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't find logged user");

		Optional< VoteEntity > vote = voteRepository.findByCitizenID_CitizenIDAndVotingID_VotingID( id, optCurUser.get( ).getCitizenID( ) );
		if ( vote.isPresent( ) ) {
			if ( voting.getVotingType( ).equals( VotingEntity.TypeOfVoting.SEJM ) )
				model.addObject( "th_redirect", "/parlament/sejm" );
			else
				model.addObject( "th_redirect", "/parlament/senat" );
			model.setViewName( "418_REPEAT_VOTE" );
			return model;
		}

		model.addObject( "voting", voting );
		model.addObject( "id", id );
		model.setViewName( "parliamentVoting" );
		return model;
	}

	@PostMapping ( value = { "/vote/{id}" } )
	public ModelAndView parlamentVoteSubmit( @PathVariable long id, @RequestParam ( "votingRadio" ) long radio, final HttpServletRequest request ) {
		ModelAndView model = new ModelAndView( );
		VoteEntity vote = new VoteEntity( );
		vote.setOptionID( optionRepository.findByOptionID( radio ).get( ) );
		Authentication auth = SecurityContextHolder.getContext( ).getAuthentication( );
		vote.setCitizenID( citizenRepository.findByEmail( auth.getName( ) ).get( ) );
		vote.setVotingID( votingRepository.findByVotingID( id ) );
		LocalTime time = LocalTime.now( );
		LocalDate date = LocalDate.now( );
		VotingEntity voting = vote.getVotingID( );
		Optional< VoteEntity > voteControl = voteRepository.findByCitizenIdVotingId( id, vote.getCitizenID( ).getCitizenID( ) );
		Optional<CitizenEntity> citizen = citizenRepository.findByEmail(request.getRemoteUser());
		if(citizen.isPresent()) {
			if (voteControl.isPresent()) {
				if (voting.getVotingType().equals(VotingEntity.TypeOfVoting.SEJM)) {
					model.addObject("th_redirect", "/parlament/sejm");
					logR.save(Log.failedAddVoteParlam("Failure to add Sejm vote - vote already exists", citizen.get()));
				}else {
					model.addObject("th_redirect", "/parlament/senat");
					logR.save(Log.failedAddVoteParlam("Failure to add Senat vote - vote already exists", citizen.get()));
				}
				model.setViewName("418_REPEAT_VOTE");
				return model;
			}
			if (voting.getCloseVoting().before(java.sql.Time.valueOf(time)) || !voting.getVotingDate().equals(java.sql.Date.valueOf(date))) {
				model.setViewName("timeOut");
				if (voting.getVotingType().equals(VotingEntity.TypeOfVoting.SEJM)) {
					model.addObject("type", "/parlament/sejm");
					logR.save(Log.failedAddVoteParlam("Failure to add Sejm vote - time out", citizen.get()));
				} else {
					model.addObject("type", "/parlament/senat");
					logR.save(Log.failedAddVoteParlam("Failure to add Senat vote - time out", citizen.get()));
				}
				return model;
			}
			vote.setVoteTimestamp(new Timestamp(System.currentTimeMillis()));
			VoteEntity check = voteRepository.save(vote);
			if (voting.getVotingType().equals(VotingEntity.TypeOfVoting.SEJM))
				logR.save(Log.successAddVoteParlam("Add Sejm vote successfully", check));
			else
				logR.save(Log.successAddVoteParlam("Add Senat vote successfully", check));
		}
		RedirectView redirect = new RedirectView( );
		if ( voting.getVotingType( ).equals( VotingEntity.TypeOfVoting.SEJM ) )
			redirect.setUrl( "/parlament/sejm" );
		else
			redirect.setUrl( "/parlament/senat" );
		return new ModelAndView( redirect );
	}

	@GetMapping ( value = { "/vote/zmianaDaty/{id}" } )
	public Object votingDateChange( @PathVariable long id, @RequestParam ( value = "dateForm", required = false ) Date dateForm, @RequestParam ( value = "timeFormOd", required = false ) Time timeFormOd, @RequestParam ( value = "timeFormDo", required = false ) Time timeFormDo, final HttpServletRequest request ) {
		VotingEntity voting = votingRepository.findByVotingID( id );
		if (voting == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Voting not found");

		ModelAndView model = new ModelAndView();
		Optional<CitizenEntity> citizen = citizenRepository.findByEmail(request.getRemoteUser());
		if(citizen.isPresent()) {
			Time timeSec = java.sql.Time.valueOf(LocalTime.now());
			java.util.Date dateSec = java.sql.Date.valueOf(LocalDate.now());
			boolean ended = (voting.getVotingDate().before(dateSec) || (voting.getVotingDate().equals(dateSec) && voting.getCloseVoting().before(timeSec)));
			boolean during = (voting.getVotingDate().equals(dateSec) && voting.getOpenVoting().before(timeSec) && voting.getCloseVoting().after(timeSec));
			if (ended || during) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Voting is ongoing or has ended");
			}

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			boolean hasUserRoleAdmin = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
			boolean hasUserRoleSejm = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_MARSZALEK_SEJMU"));
			boolean hasUserRoleSenat = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_MARSZALEK_SENATU"));

			if (voting.getVotingType() == VotingEntity.TypeOfVoting.SEJM)
				if (!(hasUserRoleAdmin || hasUserRoleSejm)) {
					logR.save(Log.failedEditVoting("Edition of sejm voting failure - forbidden for rule other than admin, Marszalek Sejmu or Prezydent", voting, citizen.get()));
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin or Marszalek Sejmu or Prezydent can change voting date");
				}

			if (voting.getVotingType() == VotingEntity.TypeOfVoting.SENAT)
				if (!(hasUserRoleAdmin || hasUserRoleSenat)) {
					logR.save(Log.failedEditVoting("Edition of senat voting failure - forbidden for rule other than admin, Marszalek Senatu or Prezydent", voting, citizen.get()));
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin or Marszalek Senatu or Prezydent can change voting date");
				}

			if (voting.getVotingType() != VotingEntity.TypeOfVoting.SEJM && voting.getVotingType() != VotingEntity.TypeOfVoting.SENAT) {
				logR.save(Log.failedEditVoting("Edition of voting failure - wrong voting type", voting, citizen.get()));
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wrong voting type");
			}

			//validacja
			String error = null;
			if (dateForm != null && timeFormOd != null && timeFormDo != null) {
				LocalDateTime nowLDT = LocalDateTime.now();
				String odDT = dateForm + "T" + timeFormOd;
				String doDT = dateForm + "T" + timeFormDo;
				LocalDateTime odLDT = LocalDateTime.parse(odDT);
				LocalDateTime doLDT = LocalDateTime.parse(doDT);

				if (odLDT.isBefore(nowLDT))
					error = "Czas rozpoczecia głosowania nie może być z przeszłości";
				if (doLDT.isBefore(odLDT))
					error = "Czas zakończenia wcześniej niż rozpoczęcia ";
			   logR.save(Log.failedEditVoting("Edition of voting failure - wrong time or date", voting, citizen.get()));
				model.addObject("error", error);
			}

			//Wyslano zadanie zmiany daty, nie ma errora, zmieniamy
			if (error == null && dateForm != null && timeFormOd != null && timeFormDo != null) {
				voting.setVotingDate(dateForm);
				voting.setOpenVoting(timeFormOd);
				voting.setCloseVoting(timeFormDo);

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
		model.addObject( "timeFrom", voting.getOpenVoting( ) );
		model.addObject( "timeTo", voting.getCloseVoting( ) );
		model.addObject( "refName", voting.getDocumentID( ).getDocName( ) );
		model.setViewName( "changeEventDate/changeVote" );
		return model;
	}

	@GetMapping(value = {"/funkcyjni"})
	public ModelAndView function(){
		ModelAndView model = new ModelAndView();
		model.addObject("sejm", parliamentarianRepository.findAllByChamberOfDeputies("Sejm"));
		model.addObject("senat", parliamentarianRepository.findAllByChamberOfDeputies("Senat"));
		model.addObject("prezydent", roleRepository.findByName("ROLE_PREZYDENT").get().getUsers().get(0));
		model.addObject("marszalek_sejm",parliamentarianRepository.findByPoliticianID_CitizenIDIn(roleRepository.findByName("ROLE_MARSZALEK_SEJMU").get().getUsers()).get(0));
		model.addObject("marszalek_senat",parliamentarianRepository.findByPoliticianID_CitizenIDIn(roleRepository.findByName("ROLE_MARSZALEK_SENATU").get().getUsers()).get(0));
		model.setViewName("funkcyjni");
		return model;
	}

	@GetMapping(value = {"/funkcyjni/{id}"})
	public ModelAndView functionID(@PathVariable Long id){
		if (id == 0){
			ModelAndView model = new ModelAndView();
			model.addObject("president",roleRepository.findByName("ROLE_PREZYDENT").get().getUsers().get(0));
			model.addObject("fn","Prezydent");
			model.setViewName("funkcyjniSzczegoly");
			return model;
		}
		Optional<ParliamentarianEntity> a = parliamentarianRepository.findById(id);
		if ( a.isEmpty() ) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person doesn't exist");
		}
		ModelAndView model = new ModelAndView();
		model.addObject("politician",a.get());
		List<CitizenEntity>b=new ArrayList<>();
		b.add(a.get().getPoliticianID().getCitizenID());
		int fn=0;
		List<Role>c=new ArrayList<>(roleRepository.findByUsersIsIn(b));
		//c.addAll(roleRepository.findByUsersIsIn(b));
		for(Role i:c){
			if(i.getName().equals("ROLE_MARSZALEK_SEJMU"))
				fn=3;
			else if (i.getName().equals("ROLE_MARSZALEK_SENATU"))
				fn=4;
			else if (i.getName().equals("ROLE_POSEL") & fn <3)
				fn=1;
			else if (i.getName().equals("ROLE_SENATOR") & fn <3)
				fn=2;
		}
		switch(fn){
			case 3:
				model.addObject("fn","Marszałek Sejmu");
				break;
			case 4:
				model.addObject("fn","Marszałek Senatu");
				break;
			case 1:
				model.addObject("fn","Poseł");
				break;
			case 2:
				model.addObject("fn","Senator");
				break;
		}
		model.setViewName("funkcyjniSzczegoly");
		return model;
	}

	@GetMapping("/przeszleGlosowania")
	public ModelAndView pastVotings(){
		return new ModelAndView("historiaParlament");
	}

	@GetMapping("/przeszleGlosowania/sejm")
	public ModelAndView sejm(){
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "pastVoting" );
		createVotingList.past( modelAndView, VotingEntity.TypeOfVoting.SEJM, votingRepository );
		return modelAndView;
	}

	@GetMapping("/przeszleGlosowania/senat")
	public ModelAndView senat(){
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "pastVoting" );
		createVotingList.past( modelAndView, VotingEntity.TypeOfVoting.SENAT, votingRepository );
		return modelAndView;
	}
}


