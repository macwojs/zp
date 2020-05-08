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
import java.util.ArrayList;
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
		modelAndView.addObject("ErrorList", null);
		modelAndView.setViewName( "presidentVotingAdd" );
		return modelAndView;
	}

	@PostMapping(value= {"/prezydenckie/planAdd"})
	public ModelAndView prezydentSubmit(@RequestParam Map<String,String>reqParameters) throws ParseException
	{
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "glosowania" );
		String data = reqParameters.remove("date");
		SetEntity set = new SetEntity("Wybory Prezydenckie "+ data);
		LocalDate time = timeVerify(data,7); //TODO time verify
		if (time == null || reqParameters.size()<3 || reqParameters.containsValue(""))
		{
			ArrayList<String> errors =  errorsMsg(time, 7,reqParameters);
			modelAndView.setViewName( "presidentVotingAdd" );
			modelAndView.addObject("ErrorList",errors);
			return modelAndView;
		}
		setSession.save(set);
		votingSession.save(new VotingEntity(
				java.sql.Date.valueOf(time),
				java.sql.Time.valueOf(LocalTime.parse("06:00:00")),
				java.sql.Time.valueOf(LocalTime.parse("21:00:00")),
				set,
				null,
				VotingEntity.TypeOfVoting.PREZYDENT,
				"Wybory Prezydenckie "+data));
		for (Map.Entry<String,String> entry: reqParameters.entrySet()) {
			if(entry.getKey().equals("_csrf")) continue;
			OptionEntity option = new OptionEntity(entry.getValue());
			optionSession.save(option);
			optionSetSession.save(new OptionSetEntity(option,set));
		}
		return modelAndView;
	}

	public LocalDate timeVerify( String time ,int delay)
	{
		if (time.isEmpty()) return null;
		LocalDate now = java.time.LocalDate.now();
		now = now.plusDays(delay);
		LocalDate res = LocalDate.parse(time);
		if (now.isAfter(res)) return  null;
		return res;
	}

	public ArrayList<String> errorsMsg(LocalDate date,int delay,Map<String,String>param )
	{
		ArrayList<String> res = new ArrayList<String>();
		if (date==null)
		{
			res.add("wydarzenie musi być zaplanowane z " + delay + " dniowym wyprzedzeniem\n");
		}
		if (param.size()<3)
		{
			res.add("musisz podać przynajmnej 2 kandydatów\n");
		}
		if (param.containsValue(""))
		{
			res.add("dane kandydata nie mogą być puste\n");
		}
		return res;
	}

}

