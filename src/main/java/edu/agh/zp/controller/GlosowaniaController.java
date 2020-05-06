package edu.agh.zp.controller;

import edu.agh.zp.objects.VotingEntity;
import edu.agh.zp.repositories.OptionRepository;
import edu.agh.zp.repositories.OptionSetRepository;
import edu.agh.zp.repositories.SetRepository;
import edu.agh.zp.repositories.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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

	@GetMapping (value= {"/prezydenckie"})
	public ModelAndView prezydentForm() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "presidentVotingAdd" );
		modelAndView.addObject(new VotingEntity());
		return modelAndView;
	}
}
