package edu.agh.zp.controller;

import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.OptionRepository;
import edu.agh.zp.repositories.OptionSetRepository;
import edu.agh.zp.repositories.SetRepository;
import edu.agh.zp.repositories.VotingRepository;
import edu.agh.zp.services.CitizenService;
import edu.agh.zp.services.ParliamentarianService;
import edu.agh.zp.services.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


import java.util.*;

@Controller
public class StatisticsController {
    @Autowired
    private VotingRepository vr;

    @Autowired
    private VoteService voteS;

    @Autowired
    private ParliamentarianService parlS;

    @Autowired
    private CitizenService cS;

    @Autowired
    private OptionSetRepository osR;

    @Autowired
    private OptionRepository oR;

    @GetMapping("/kalendarz/wydarzenie/{num}/wyniki")
    public ModelAndView results(@PathVariable Long num) {
        VotingEntity voting = vr.findByVotingID(num);
        if(voting==null) {
            return new ModelAndView(String.valueOf(HttpStatus.NOT_FOUND));
        }
        Statistics stats = new Statistics();
        Chart pieChart = new Chart( "Rozkład głosów");
        List<Chart> multiChart = new ArrayList<>();

        List<String> politicalGroups = parlS.findPoliticalGroups();
        for(String group : politicalGroups){
            multiChart.add(new Chart(group));
        }
        List<OptionSetEntity> tempOptions = osR.findAllBySetIDcolumn( voting.getSetID_column() );
        for( OptionSetEntity i : tempOptions ){
            Optional<OptionEntity> temp = oR.findByOptionID( i.getOptionID().getOptionID() );
            if(temp.isPresent()){
                OptionEntity option = temp.get(); // option
                if(voting.getVotingType() == VotingEntity.TypeOfVoting.SEJM ||  voting.getVotingType() == VotingEntity.TypeOfVoting.SENAT  ) {
                    for (int j = 0; j < politicalGroups.size(); ++j) { // iterate through political groups to get information about votes in each of them
                        Long voteCount = voteS.findByVotingAndOptionAndPoliticalGroup(voting, option, politicalGroups.get(j));
                        multiChart.get(j).data.add(new StatisticRecord(option.getOptionDescription(), voteCount));
                    }
                }
                Long voteCount = voteS.countByVotingAndOption(voting, option);
                pieChart.data.add(new StatisticRecord(option.getOptionDescription(), voteCount));
            }
        }
        ModelAndView modelAndView = new ModelAndView( );
        switch(voting.getVotingType()){
            case SEJM:
                stats = new Statistics(voteS.countAllByVoting(voting), parlS.countMemberOfSejm(), pieChart, VotingEntity.TypeOfVoting.SEJM);
                modelAndView.addObject("multichart",  multiChart);
                break;
            case SENAT:
                stats = new Statistics(voteS.countAllByVoting(voting), parlS.countMemberOfSenat(), pieChart, VotingEntity.TypeOfVoting.SENAT);
                modelAndView.addObject("multichart",  multiChart);
                break;
            case PREZYDENT:
            case REFERENDUM:
                stats = new Statistics(voteS.countAllByVoting(voting), cS.countEntitledToVote(), pieChart, VotingEntity.TypeOfVoting.REFERENDUM);
                break;
        }

        modelAndView.setViewName( "votingResults" );
        modelAndView.addObject("voting", voting);
        modelAndView.addObject("statistics",  stats);

        return modelAndView;
    }
}
