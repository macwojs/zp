package edu.agh.zp.controller;

import com.github.javafaker.DateAndTime;
import edu.agh.zp.objects.VotingEntity;
import edu.agh.zp.repositories.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Event{
	public long id;
	public LocalDateTime start;
	public LocalDateTime end;
	public String title;

	public Event(long id, LocalDateTime start, LocalDateTime end, String title) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.title = "Głosowanie "+title;
	}
}

@Controller
@RequestMapping (value={"/kalendarz"})
public class KalendarzController {
	@Autowired
	private VotingRepository vr;

	@GetMapping (value = {""})
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "kalendarz" );
		modelAndView.addObject("voting", parseVotingsToEvents());

		return modelAndView;
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
					i.getDocumentID().getDocName()));
		}
		return events;
	}

	public LocalDateTime dateAndTimeToLocalDateTime(Date date, Time time) {
		String myDate = date + "T" + time;
		return LocalDateTime.parse(myDate);
	}
}

