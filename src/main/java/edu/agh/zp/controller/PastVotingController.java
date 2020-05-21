package edu.agh.zp.controller;

import edu.agh.zp.objects.VotingEntity;
import edu.agh.zp.objects.createVotingList;
import edu.agh.zp.repositories.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value={"/historia"})
public class PastVotingController{

    @Autowired
    private VotingRepository votingRepository;

    @GetMapping("")
    public ModelAndView index(){
        return new ModelAndView("historia");
    }

    @GetMapping("/sejm")
    public ModelAndView sejm(){
        ModelAndView modelAndView = new ModelAndView( );
        modelAndView.setViewName( "pastVoting" );
        createVotingList.past( modelAndView, VotingEntity.TypeOfVoting.SEJM, votingRepository );
        return modelAndView;
    }

    @GetMapping("/senat")
    public ModelAndView senat(){
        ModelAndView modelAndView = new ModelAndView( );
        modelAndView.setViewName( "pastVoting" );
        createVotingList.past( modelAndView, VotingEntity.TypeOfVoting.SENAT, votingRepository );
        return modelAndView;
    }

    @GetMapping("/prezydent")
    public ModelAndView prezydent(){
        ModelAndView modelAndView = new ModelAndView( );
        modelAndView.setViewName( "pastVoting" );
        createVotingList.past( modelAndView, VotingEntity.TypeOfVoting.PREZYDENT, votingRepository );
        return modelAndView;
    }

    @GetMapping("/referendum")
    public ModelAndView referendum(){
        ModelAndView modelAndView = new ModelAndView( );
        modelAndView.setViewName( "pastVoting" );
        createVotingList.past( modelAndView, VotingEntity.TypeOfVoting.REFERENDUM, votingRepository );
        return modelAndView;
    }
}
