package edu.agh.zp.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping( value = { "/obywatel" } )
public class CitizenController  {

	@GetMapping(value = {"dane"})
	public ModelAndView citizenDetails(){
		ModelAndView model = new ModelAndView();

		return model;
	}

}