package edu.agh.zp.controller;

import edu.agh.zp.objects.OptionEntity;
import edu.agh.zp.objects.OptionSetEntity;
import edu.agh.zp.objects.SetEntity;
import edu.agh.zp.objects.VotingEntity;
import edu.agh.zp.repositories.OptionRepository;
import edu.agh.zp.repositories.OptionSetRepository;
import edu.agh.zp.repositories.SetRepository;
import edu.agh.zp.repositories.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Controller
@RequestMapping (value={"/glosowania"})
public class GlosowaniaController {

	@Autowired
	VotingRepository votingSession;

	@Autowired
	OptionSetRepository optionSetSession;

	@Autowired
	OptionRepository optionSession;

	@Autowired
	SetRepository setSession;

	@GetMapping (value = {""})
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "glosowania" );
		return modelAndView;
	}

	@GetMapping (value= {"/prezydenckie/plan"})
	public ModelAndView prezydentForm() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "presidentVotingAdd" );
		modelAndView.addObject(new VotingEntity());
		return modelAndView;
	}

	@PostMapping(value= {"/prezydenckie/planAdd"})
	public ModelAndView prezydentSubmit(@RequestParam Map<String,String>reqParameters) throws ParseException {
		String data = reqParameters.remove("date");
		SetEntity set = new SetEntity("Wybory Prezydenckie "+ data);
		setSession.save(set);
		for (Map.Entry<String,String> entry: reqParameters.entrySet()) {
			if(entry.getKey().equals("_csrf")) continue;
			OptionEntity option = new OptionEntity(entry.getValue());
			optionSession.save(option);
			optionSetSession.save(new OptionSetEntity(option,set));
		}
		LocalDate time = LocalDate.parse(data); //TODO time verify
		votingSession.save(new VotingEntity(
				java.sql.Date.valueOf(time),
				java.sql.Time.valueOf(LocalTime.parse("06:00:00")),
				java.sql.Time.valueOf(LocalTime.parse("21:00:00")),
				set,
				null,
				VotingEntity.TypeOfVoting.PREZYDENT,
				"Wybory Prezydenckie "+data));
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "glosowania" );
		modelAndView.addObject(new VotingEntity());
		return modelAndView;
	}
}

