package edu.agh.zp.controller;

import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

@Controller
@RequestMapping(value = {"/wyboryReferenda"})
public class WyboryController {

    @Autowired
    VotingRepository votingSession;

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


    @GetMapping(value = {""})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        createVotingList.run(modelAndView, Arrays.asList(VotingEntity.TypeOfVoting.REFERENDUM,VotingEntity.TypeOfVoting.PREZYDENT) , votingSession);
        Th_min min = new Th_min();
        modelAndView.addObject("min",min);
        modelAndView.setViewName("wyboryReferenda");
        return modelAndView;
    }


    @GetMapping(value = {"/{id}"})
    public ModelAndView referendumVote(ModelAndView model, @PathVariable long id, Principal principal) {
        Optional<CitizenEntity> optCurUser = citizenSession.findByEmail(principal.getName());
        if (optCurUser.isEmpty()) {
            model.setViewName("signin");
            return model;
        }
        VotingEntity voting = votingSession.findByVotingID(id);
        Optional<VotingControlEntity> votingControl = votingControlSession.findByCitizenIDAndVotingID(optCurUser.get(), voting);
        if (votingControl.isPresent()) {
            model.addObject("th_redirect", "/wyboryReferenda");
            model.setViewName("418_REPEAT_VOTE");
            return model;
        }
        model.addObject("voting", voting);
        model.addObject("id", id);
        if (voting.getVotingType().equals(VotingEntity.TypeOfVoting.REFERENDUM)) model.setViewName("referendumVoting");
        else model.setViewName("presidentVoting");
        return model;
    }

    @PostMapping(value = {"/{id}"})
    public ModelAndView referendumVoteSubmit( @PathVariable long id, @RequestParam( "votingRadio" ) long radio ) {
        VoteEntity vote = new VoteEntity( );
        vote.setOptionID( optionSession.findById( radio ).get( ) );
        Authentication auth = SecurityContextHolder.getContext( ).getAuthentication( );

        vote.setVotingID( votingSession.findByVotingID( id ) );
        LocalTime time = LocalTime.now( );
        LocalDate date = LocalDate.now( );
        VotingEntity voting = vote.getVotingID( );
        if ( voting.getCloseVoting( ).before( java.sql.Time.valueOf( time ) ) || !voting.getVotingDate( ).equals( java.sql.Date.valueOf( date ) ) ) {
            ModelAndView model = new ModelAndView( );
            model.setViewName( "timeOut" );
            if ( voting.getVotingType( ).equals( VotingEntity.TypeOfVoting.SEJM ) )
                model.addObject( "type", "/wyboryReferenda" );
            else
                model.addObject( "type", "/wyboryReferenda" );
            return model;
        }
        vote.setVoteTimestamp( new Timestamp( System.currentTimeMillis( ) ) );
        voteSession.save( vote );
        votingControlSession.save(new VotingControlEntity(citizenSession.findByEmail( auth.getName( ) ).get( ),voting));
        RedirectView redirect = new RedirectView( );
        if ( voting.getVotingType( ).equals( VotingEntity.TypeOfVoting.SEJM ) )
            redirect.setUrl( "/parlament/sejm" );
        else
            redirect.setUrl( "/parlament/senat" );
        return new ModelAndView( redirect );
    }


}

class Th_min{
    public int run(int x,int y){
        return Math.min(x, y);
    }
}