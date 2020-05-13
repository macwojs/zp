package edu.agh.zp.controller;

import edu.agh.zp.objects.DocumentEntity;
import edu.agh.zp.objects.SetEntity;
import edu.agh.zp.objects.VoteEntity;
import edu.agh.zp.objects.VotingEntity;
import edu.agh.zp.repositories.CitizenRepository;
import edu.agh.zp.repositories.DocumentRepository;
import edu.agh.zp.repositories.SetRepository;
import edu.agh.zp.repositories.VotingRepository;
import edu.agh.zp.services.CitizenService;
import org.hibernate.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.security.sasl.AuthorizeCallback;
import javax.validation.Valid;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
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
	private VotingRepository votingRepository;

	@Autowired
    private CitizenRepository citizenRepository;

	@GetMapping ( value = { "" } )
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "sejm" );
		LocalDate now = LocalDate.now();
		Date nowSql = java.sql.Date.valueOf(now);
		List <VotingEntity> voting = votingRepository.findByVotingDateBetweenOrderByVotingDateAscOpenVotingAsc(nowSql,java.sql.Date.valueOf(now.plusDays(1)));
		boolean[] nums = new boolean[voting.size()];
		Time time = java.sql.Time.valueOf(LocalTime.now());
		if (voting.isEmpty()) modelAndView.addObject("Sign",null);
		else {
			for (int i = 0; i<voting.size(); i++) {
				VotingEntity vote = voting.get(i);
				if (vote.getVotingDate().equals(nowSql) && vote.getOpenVoting().before(time) && vote.getCloseVoting().after(time)) nums[i] = true;
			}
			modelAndView.addObject("Sign",nums);
		}
		modelAndView.addObject("VotingList",voting);
		return modelAndView;
	}

	@GetMapping ( value = { "/voteAdd" } )
	public ModelAndView sejmVoteAdd( ModelAndView model ) {
		List< DocumentEntity > documents = documentRepository.findByDocForSejm();
		Optional< SetEntity > set = setRepository.findById((long)1);
		if(set.isPresent()) {
			model.addObject("documents", documents);
			model.addObject("voting", new VotingEntity());
		}
		model.setViewName( "sejmVotingAdd" );
		return model;
	}

	@PostMapping ( value = { "/voteAdd" } )
	public ModelAndView documentFormSubmit( @Valid @ModelAttribute ( "voting" ) VotingEntity voting, BindingResult res ) throws ParseException {
		if ( res.hasErrors( ) ) {
			for( Object i : res.getAllErrors()){
				System.out.print("\n"+i.toString()+"\n");
			}

			//Musi być ponownie dodane, bo inaczej nie wypełnia listy
			ModelAndView model = new ModelAndView(  );
			List< DocumentEntity > documents = documentRepository.findByDocForSejm();
			model.addObject("documents", documents);
			model.setViewName( "sejmVotingAdd" );
			return model;
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
		redirect.setUrl( "/parlament/sejm" );
		return new ModelAndView( redirect );
	}

	@GetMapping ( value = { "/vote/{id}" } )
	public ModelAndView sejmVote( ModelAndView model, @PathVariable long id ) {
        VoteEntity vote = new VoteEntity();
        vote.setVotingID(votingRepository.findByVotingID(id));
        //TODO Sprawdź, czy już nie głosował!!!
        //<a class="login"  sec:authorize="isAuthenticated()"  sec:authentication="name"></a>
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        vote.setCitizenID(citizenRepository.findByEmail(auth.getName()).get());
	    model.addObject("vote",vote);
		model.setViewName( "parliamentVoting" );
		return model;
	}
}
