package edu.agh.zp.controller;

import edu.agh.zp.repositories.CitizenRepository;
import edu.agh.zp.objects.CitizenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping ( value = { "/obywatel" } )
public class CitizenController {

	@Autowired
	private CitizenRepository citizenRepository;

	@GetMapping ( value = { "dane" } )
	public ModelAndView citizenDetails( Principal principal ) {
		ModelAndView model = new ModelAndView( );
		Optional< CitizenEntity > currentCitizen = citizenRepository.findByEmail( principal.getName( ) );
	    if (currentCitizen.isPresent())
	    {
	    	model.addObject( "citizen", currentCitizen.get() );
	    }
	    model.setViewName( "citizenDetails" );
		return model;
	}

}