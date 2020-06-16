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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @Autowired
    LogRepository logR;

    @GetMapping(value = {""})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        createVotingList.future(modelAndView, Arrays.asList(VotingEntity.TypeOfVoting.REFERENDUM, VotingEntity.TypeOfVoting.PREZYDENT), votingSession);
        Th_min min = new Th_min();
        modelAndView.addObject("min", min);
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

        model.setViewName("wyboryReferendaVoting");
        List<OptionSetEntity> list = optionSetSession.findBySetIDcolumn(voting.getSetID_column());
        ArrayList<OptionEntity> options = new ArrayList<>();
        for (OptionSetEntity optionSet : list) {
            options.add(optionSet.getOptionID());
            model.addObject("options", options);
        }
        return model;
    }

    @PostMapping(value = {"/{id}"})
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
                model.addObject("th_redirect", "/wyboryReferenda");
                model.setViewName("418_REPEAT_VOTE");
                logR.save(Log.failedAddVoteCitizen("Failure to add citizen vote - vote already exists", voting, optCurUser.get()));
                return model;
            }
            if (voting.getCloseVoting().before(java.sql.Time.valueOf(time)) || !voting.getVotingDate().equals(java.sql.Date.valueOf(date))) {
                ModelAndView model = new ModelAndView();
                model.setViewName("timeOut");
                model.addObject("type", "/wyboryReferenda");
                logR.save(Log.failedAddVoteCitizen("Failure to add citizen vote - timeout", voting, optCurUser.get()));
                return model;
            }
            vote.setVoteTimestamp(new Timestamp(System.currentTimeMillis()));
            voteSession.save(vote);
            votingControlSession.save(new VotingControlEntity(citizenSession.findByEmail(auth.getName()).orElseThrow(() -> new Exception( "Citizen Not Found" )), voting));
            logR.save(Log.successAddVoteCitizen("Add vote to citizen voting.", voting, optCurUser.orElseThrow(() -> new Exception( "Citizen Not Found" ))));
        }
        RedirectView redirect = new RedirectView();
        redirect.setUrl("/wyboryReferenda");
        return new ModelAndView(redirect);
    }


}

class Th_min {
    public int run(int x, int y) {
        return Math.min(x, y);
    }
}