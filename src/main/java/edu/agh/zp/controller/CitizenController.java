package edu.agh.zp.controller;

import edu.agh.zp.classes.Th_min;
import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping ( value = { "/obywatel" } )
public class CitizenController {

	@Autowired
	private CitizenRepository citizenRepository;

	@Autowired
	VotingRepository votingSession;

	@Autowired
	private VotingRepository votingRepository;

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
	CitizenRepository citizenSession;

	@Autowired
	VoteRepository voteSession;

	@Autowired
	LogRepository logR;

	@GetMapping ( value = { "dane" } )
	public ModelAndView citizenDetails( Principal principal ) {
		ModelAndView model = new ModelAndView( );
		Optional< CitizenEntity > currentCitizen = citizenRepository.findByEmail( principal.getName( ) );
		currentCitizen.ifPresent(citizenEntity -> model.addObject("citizen", citizenEntity));
	    model.setViewName( "citizenDetails" );
		return model;
	}

	@GetMapping ( value = { "dane/modify/{scenario}" } )
	public ModelAndView citizenModify(Principal principal, @PathVariable String scenario) {
		ModelAndView model = new ModelAndView( );
		Optional< CitizenEntity > currentCitizen = citizenRepository.findByEmail( principal.getName( ) );
		if (currentCitizen.isEmpty()) {
			model.setViewName("index");
			return model;
		}
		model.addObject("sceanario",scenario);
		model.setViewName("citizenModify");
		model.addObject("err", null);
		return model;
	}

	@PostMapping( value = { "dane/modify/{scenario}" } )
	public ModelAndView citizenPut(Principal principal, @RequestParam("field1") String f1, @RequestParam("field2") String f2, @PathVariable String scenario) {
		ModelAndView model = new ModelAndView( );
		Optional< CitizenEntity > currentCitizen = citizenRepository.findByEmail( principal.getName( ) );
		if (currentCitizen.isEmpty()) {
			model.setViewName("index");
			return model;
		}
		RedirectView redirect = new RedirectView();
		CitizenEntity citizen = currentCitizen.get();
		switch (scenario){
			case "adr":
				citizenRepository.updateAddress(citizen.getCitizenID(),f1,f2);
				citizen.setTown(f1);
				citizen.setAddress(f2);
				break;
			case "mail":
				if (citizenRepository.findByEmail(f1).isPresent())
				{
					model.addObject("err", "email zajęty");
					model.addObject("sceanario",scenario);
					model.setViewName("citizenModify");
					return model;
				}
				citizenRepository.updateEmail(citizen.getCitizenID(),f1);
				redirect.setUrl("/logout");
				return new ModelAndView(redirect);
			case "pass":
				if (f1.equals(f2)){
					if (f1.length()<8){
						model.addObject("err", "hasło musi zawierać conajmej 8 znaków");
						model.addObject("sceanario",scenario);
						model.setViewName("citizenModify");
						return model;
					}
					citizenRepository.updatePass(citizen.getCitizenID(),BCrypt.hashpw(f1, BCrypt.gensalt()));
					redirect.setUrl("/logout");
					return new ModelAndView(redirect);
				}
				else {
					model.addObject("err", "hasła nie zgadzają się");
					model.addObject("sceanario",scenario);
					model.setViewName("citizenModify");
					return model;
				}
		}
		model.addObject( "citizen", citizen );
		model.setViewName( "citizenDetails" );
		return model;
	}

	@GetMapping(value = {"wyboryReferenda"})
	public ModelAndView wyboryReferenda() {
		ModelAndView modelAndView = new ModelAndView();
		createVotingList.future(modelAndView, Arrays.asList( VotingEntity.TypeOfVoting.REFERENDUM, VotingEntity.TypeOfVoting.PREZYDENT), votingSession);
		Th_min min = new Th_min();
		modelAndView.addObject("min", min);
		modelAndView.setViewName("wyboryReferenda");
		return modelAndView;
	}

	@GetMapping(value = {"wyboryReferenda/{id}"})
	public ModelAndView referendumVote(ModelAndView model, @PathVariable long id, Principal principal) {
		Optional<CitizenEntity> optCurUser = citizenSession.findByEmail(principal.getName());
		if (optCurUser.isEmpty()) {
			model.setViewName("signin");
			return model;
		}
		VotingEntity voting = votingSession.findByVotingID(id);
		Optional< VotingControlEntity > votingControl = votingControlSession.findByCitizenIDAndVotingID(optCurUser.get(), voting);
		if (votingControl.isPresent()) {
			model.addObject("th_redirect", "/obywatel/wyboryReferenda");
			model.setViewName("418_REPEAT_VOTE");
			return model;
		}
		model.addObject("voting", voting);
		model.addObject("id", id);

		model.setViewName("wyboryReferendaVoting");
		List< OptionSetEntity > list = optionSetSession.findBySetIDcolumn(voting.getSetID_column());
		ArrayList<OptionEntity> options = new ArrayList<>();
		for (OptionSetEntity optionSet : list) {
			options.add(optionSet.getOptionID());
			model.addObject("options", options);
		}
		return model;
	}

	@PostMapping(value = {"wyboryReferenda/{id}"})
	public ModelAndView referendumVoteSubmit(@PathVariable long id, @RequestParam("votingRadio") long radio, Principal principal) throws Exception {
		VoteEntity vote = new VoteEntity();
		vote.setOptionID(optionSession.findById(radio).orElseThrow(() -> new Exception( "Option Not Found" )));
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		vote.setVotingID(votingSession.findByVotingID(id));
		LocalTime time = LocalTime.now();
		LocalDate date = LocalDate.now();
		VotingEntity voting = vote.getVotingID();
		Optional<CitizenEntity> optCurUser = citizenSession.findByEmail(principal.getName());
		if(optCurUser.isPresent()) {
			Optional<VotingControlEntity> votingControl = votingControlSession.findByCitizenIDAndVotingID(optCurUser.get(), voting);
			if (votingControl.isPresent()) {
				ModelAndView model = new ModelAndView();
				model.addObject("th_redirect", "/obywatel/wyboryReferenda");
				model.setViewName("418_REPEAT_VOTE");
				logR.save(Log.failedAddVoteCitizen("Failure to add citizen vote - vote already exists", voting, optCurUser.get()));
				return model;
			}
			if (voting.getCloseVoting().before(java.sql.Time.valueOf(time)) || !voting.getVotingDate().equals(java.sql.Date.valueOf(date))) {
				ModelAndView model = new ModelAndView();
				model.setViewName("timeOut");
				model.addObject("type", "/obywatel/wyboryReferenda");
				logR.save(Log.failedAddVoteCitizen("Failure to add citizen vote - timeout", voting, optCurUser.get()));
				return model;
			}
			vote.setVoteTimestamp(new Timestamp(System.currentTimeMillis()));
			voteSession.save(vote);
			votingControlSession.save(new VotingControlEntity(citizenSession.findByEmail(auth.getName()).orElseThrow(() -> new Exception( "Citizen Not Found" )), voting));
			logR.save(Log.successAddVoteCitizen("Add vote to citizen voting.", voting, optCurUser.orElseThrow(() -> new Exception( "Citizen Not Found" ))));
		}
		RedirectView redirect = new RedirectView();
		redirect.setUrl("/obywatel/wyboryReferenda");
		return new ModelAndView(redirect);
	}

	@GetMapping("przeszleGlosowania")
	public ModelAndView index(){
		return new ModelAndView("historiaObywatel");
	}

	@GetMapping("przeszleGlosowania/prezydent")
	public ModelAndView prezydent(){
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "pastVoting" );
		createVotingList.past( modelAndView, VotingEntity.TypeOfVoting.PREZYDENT, votingRepository );
		return modelAndView;
	}

	@GetMapping("przeszleGlosowania/referendum")
	public ModelAndView referendum(){
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "pastVoting" );
		createVotingList.past( modelAndView, VotingEntity.TypeOfVoting.REFERENDUM, votingRepository );
		return modelAndView;
	}

}