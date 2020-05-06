package edu.agh.zp.controller;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.services.CitizenService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping (value={"/signup"})
public class SignUpController {
	private CitizenService cS;
	public SignUpController(CitizenService cS){
		this.cS = cS;
	}

	@GetMapping (value = {""})
	public ModelAndView index() {
		String viewName = "signup";
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", new CitizenEntity());
		return new ModelAndView(viewName, model);
	}

	@PostMapping("")
	public ModelAndView submitRegister(@Valid @ModelAttribute("user") CitizenEntity citizen, BindingResult res, Model model){
		if( res.hasErrors()){
			return new ModelAndView("signup");
		}else{
			Optional<CitizenEntity> exists = cS.findByEmail(citizen.getEmail());
			if(exists.isPresent()){
				model.addAttribute("userError","A user with this email already exist");
				return new ModelAndView("signup");
			}
			exists = cS.findByPesel(citizen.getPesel());
			if(exists.isPresent()){
				model.addAttribute("userError","A user with this pesel already exist");
				return new ModelAndView("signup");
			}
			exists = cS.findByIdNumer(citizen.getIdNumber());
			if(exists.isPresent()){
				model.addAttribute("userError","A user with this IdNumber already exist");
				return new ModelAndView("signup");
			} else {
				citizen.setPassword(BCrypt.hashpw(citizen.getPassword(), BCrypt.gensalt()));
				cS.create(citizen);
			}
		}
		RedirectView redirect = new RedirectView();
		redirect.setUrl("");
		return new ModelAndView(redirect);
	}
}
