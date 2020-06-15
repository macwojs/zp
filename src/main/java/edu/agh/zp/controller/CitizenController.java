package edu.agh.zp.controller;

import edu.agh.zp.repositories.CitizenRepository;
import edu.agh.zp.objects.CitizenEntity;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;
import java.util.regex.Pattern;

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

	@GetMapping ( value = { "dane/modify/{scenario}" } )
	public ModelAndView citizenModify(Principal principal, @PathVariable String scenario) {
		ModelAndView model = new ModelAndView( );
		Optional< CitizenEntity > currentCitizen = citizenRepository.findByEmail( principal.getName( ) );
		if (currentCitizen.isEmpty()) {
			model.setViewName("index");
			return model;
		}
		model.addObject("sceanario",scenario);
		model.setViewName("citizenModify");
		model.addObject("err", null);
		return model;
	}

	@PostMapping( value = { "dane/modify/{scenario}" } )
	public ModelAndView citizenPut(Principal principal, @RequestParam("field1") String f1, @RequestParam("field2") String f2, @PathVariable String scenario) {
		ModelAndView model = new ModelAndView( );
		Optional< CitizenEntity > currentCitizen = citizenRepository.findByEmail( principal.getName( ) );
		if (currentCitizen.isEmpty()) {
			model.setViewName("index");
			return model;
		}
		RedirectView redirect = new RedirectView();
		CitizenEntity citizen = currentCitizen.get();
		switch (scenario){
			case "adr":
				citizenRepository.updateAddress(citizen.getCitizenID(),f1,f2);
				citizen.setTown(f1);
				citizen.setAddress(f2);
				break;
			case "mail":
				if (citizenRepository.findByEmail(f1).isPresent())
				{
					model.addObject("err", "email zajęty");
					model.addObject("sceanario",scenario);
					model.setViewName("citizenModify");
					return model;
				}
				citizenRepository.updateEmail(citizen.getCitizenID(),f1);
				redirect.setUrl("/logout");
				return new ModelAndView(redirect);
			case "pass":
				if (f1.equals(f2)){
					if (f1.length()<8){
						model.addObject("err", "hasło musi zawierać conajmej 8 znaków");
						model.addObject("sceanario",scenario);
						model.setViewName("citizenModify");
						return model;
					}
					citizenRepository.updatePass(citizen.getCitizenID(),BCrypt.hashpw(f1, BCrypt.gensalt()));
					redirect.setUrl("/logout");
					return new ModelAndView(redirect);
				}
				else {
					model.addObject("err", "hasła nie zgadzają się");
					model.addObject("sceanario",scenario);
					model.setViewName("citizenModify");
					return model;
				}
		}
		model.addObject( "citizen", citizen );
		model.setViewName( "citizenDetails" );
		return model;
	}
}