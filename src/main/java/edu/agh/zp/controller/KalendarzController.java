package edu.agh.zp.controller;

import com.github.javafaker.DateAndTime;
import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.OptionRepository;
import edu.agh.zp.repositories.OptionSetRepository;
import edu.agh.zp.repositories.VotingRepository;
import edu.agh.zp.services.ParliamentarianService;
import edu.agh.zp.services.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

class Event{
	public long id;
	public LocalDateTime start;
	public LocalDateTime end;
	public String title;
	public String url;

	public Event(long id, LocalDateTime start, LocalDateTime end, String title, String url) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.title = title;
		this.url = url;
	}
}


//
//class MultiCulumnData extends Chart{
//	public Map<String, List<StatisticRecord>> data;
//}


@Controller
@RequestMapping (value={"/kalendarz"})
public class KalendarzController {
	@Autowired
	private VotingRepository vr;

	@Autowired
	private VoteService voteS;

	@Autowired
	private ParliamentarianService parlS;

	@Autowired
	private OptionSetRepository osR;

	@Autowired
	private OptionRepository oR;


	@GetMapping (value = {""})
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "kalendarz" );
		modelAndView.addObject("voting", parseVotingsToEvents());

		return modelAndView;
	}

	@GetMapping (value = {"addevent"})
	public String addEvent() {
//		ModelAndView modelAndView = new ModelAndView( );
//		modelAndView.setViewName( "kalendarz" );
		return "addevent";
	}

	@RequestMapping(value="/allevents", method= RequestMethod.GET)
	public List<Event> allEvents() {
		return parseVotingsToEvents();
	}

	public List<Event> parseVotingsToEvents(){
		List<VotingEntity> votings = vr.findAll();
		List<Event> events = new ArrayList<>();
		for( VotingEntity i : votings) {
			events.add(new Event(i.getVotingID(),
					dateAndTimeToLocalDateTime(i.getVotingDate(),i.getOpenVoting()),
					dateAndTimeToLocalDateTime(i.getVotingDate(),i.getCloseVoting()),
					i.getDocumentID() != null ? i.getDocumentID().getDocName() :
							i.getVotingDescription() != null ? i.getVotingDescription() : i.getVotingType().toString(), "/kalendarz/wydarzenie/"+i.getVotingID()));
		}
		return events;
	}

	public LocalDateTime dateAndTimeToLocalDateTime(Date date, Time time) {
		String myDate = date + "T" + time;
		return LocalDateTime.parse(myDate);
	}

	@GetMapping("/wydarzenie/{num}")
	public ModelAndView index(@PathVariable Long num) {
		VotingEntity voting = vr.findByVotingID(num);
		if(voting==null) {
			return new ModelAndView(String.valueOf(HttpStatus.NOT_FOUND));
		}
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "wydarzenie" );
		modelAndView.addObject("voting", voting);
		//System.out.println("\n\n\n"+num+"\n\n\n");
		String link = "";
		if(voting.getVotingType()== VotingEntity.TypeOfVoting.PREZYDENT){
			link="/prezydent/vote/"+voting.getVotingID();
		}else if(voting.getVotingType()== VotingEntity.TypeOfVoting.REFERENDUM){
			link="/referendum/vote/"+voting.getVotingID();
		}else if(voting.getVotingType()== VotingEntity.TypeOfVoting.SEJM || voting.getVotingType()== VotingEntity.TypeOfVoting.SENAT){
			link="/parlament/vote/"+voting.getVotingID();
		}
		modelAndView.addObject("link", link);
		Time time = java.sql.Time.valueOf(LocalTime.now());
		Date date = java.sql.Date.valueOf(LocalDate.now());
		boolean vs = (voting.getVotingDate().equals(date) && voting.getOpenVoting().before(time) && voting.getCloseVoting().after(time));
		modelAndView.addObject("visibility",vs);
		boolean ended = (voting.getVotingDate().before(date)||(voting.getVotingDate().equals(date)&&voting.getCloseVoting().before(time)));
		modelAndView.addObject("ended", ended);
		return modelAndView;
	}

	@GetMapping("/wydarzenie/{num}/wyniki")
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
		List<OptionSetEntity> tempOptions = osR.findAllByOptionSetID_SetID( voting.getSetID_column() );
		for( OptionSetEntity i : tempOptions ){
			Optional<OptionEntity> temp = oR.findByOptionID( i.getOptionID().getOptionID() );
			if(temp.isPresent()){
				OptionEntity option = temp.get(); // option
				for(int j = 0; j < politicalGroups.size(); ++j){ // iterate through political groups to get information about votes in each of them
					Long voteCount = voteS.findByVotingAndOptionAndPoliticalGroup(voting, option, politicalGroups.get(j));
					multiChart.get(j).data.add(new StatisticRecord(option.getOptionDescription(), voteCount.toString()));
				}
				Long voteCount = voteS.countByVotingAndOption(voting, option);
				pieChart.data.add(new StatisticRecord(option.getOptionDescription(), voteCount.toString()));
			}
		}

		switch(voting.getVotingType()){
			case SEJM:
				stats = new Statistics(voteS.countAllByVoting(voting), parlS.countMemberOfSejm(), pieChart);

				break;
			case SENAT:
				stats = new Statistics(voteS.countAllByVoting(voting), parlS.countMemberOfSenat(), pieChart);
				break;
		}

		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "votingResults" );
		modelAndView.addObject("voting", voting);
		modelAndView.addObject("statistics",  stats);
		modelAndView.addObject("multichart",  multiChart);
		return modelAndView;
	}
}



