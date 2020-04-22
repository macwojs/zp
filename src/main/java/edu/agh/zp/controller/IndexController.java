package edu.agh.zp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value={""})
public class IndexController {

	@GetMapping(value = {""})
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "index" );
		return modelAndView;
	}

	@GetMapping (value = {"/hello"} )
	public ModelAndView hello(@RequestParam (value = "name", defaultValue = "World") String name) {

		String viewName = "hello";
		Map<String, Object> model = new HashMap<String, Object>();

		model.put("name", name);

		return new ModelAndView(viewName , model);
	}
}
