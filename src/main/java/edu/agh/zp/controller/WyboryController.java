package edu.agh.zp.controller;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.VotingControlEntity;
import edu.agh.zp.objects.VotingEntity;
import edu.agh.zp.objects.createVotingList;
import edu.agh.zp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
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
        modelAndView.setViewName("wyboryReferenda");
        return modelAndView;
    }


    @GetMapping(value = {"{id}"})
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


}
