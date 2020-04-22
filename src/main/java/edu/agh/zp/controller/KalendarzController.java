package edu.agh.zp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping (value={"/kalendarz"})
public class KalendarzController {

	@GetMapping (value = {""})
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "kalendarz" );
		return modelAndView;
	}
}