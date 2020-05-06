package edu.agh.zp.controller;

import edu.agh.zp.objects.VotingEntity;
import edu.agh.zp.repositories.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

class Event{
	public long id;
	public Date start;
	public Date end;
	public String title;

	public Event(long id, Date start, Date end, String title) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.title = "głosowanie "+title;
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
		List<VotingEntity> votings = vr.findAll();
		List<Event> events = new ArrayList<>();
		for( VotingEntity i : votings) {
			events.add(new Event(i.getVotingID(), i.getVotingDate(), i.getVotingDate(), i.getDocumentID().getDocName()));
		}
		modelAndView.addObject("voting", events);

		return modelAndView;
	}

	@RequestMapping(value="/allevents", method= RequestMethod.GET)
	public List<VotingEntity> allEvents() {
		return vr.findAll();
	}
}

