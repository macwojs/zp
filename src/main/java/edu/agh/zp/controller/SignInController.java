package edu.agh.zp.controller;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.services.CitizenService;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping (value={"/signin"})
public class SignInController {
	private CitizenService cS;
	/*public SignInController(CitizenService cS){
		this.cS = cS;
	}*/
	//private ApplicationContext context;

	@GetMapping (value = {""})
	public ModelAndView index() {
		String viewName = "signin" ;
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", new CitizenEntity());
		return new ModelAndView(viewName, model);
	}

	@PostMapping("")
	public ModelAndView submitLogin(@ModelAttribute("user") CitizenEntity citizen, Model model, HttpSession session){
		Optional<CitizenEntity> c = cS.findByEmail(citizen.getEmail());
		if(c.isPresent()){
			System.out.println("zly mail     " );
			model.addAttribute("userError","A user with this email don't already exist");
			return new ModelAndView("signin");
		}else if(BCrypt.checkpw(citizen.getPassword(),c.get().getPassword())) {
			System.out.println("zalogowany      " );
			session.setAttribute("user",c.get().getCitizenID());
			RedirectView redirect = new RedirectView();
			redirect.setUrl("");
			return new ModelAndView(redirect);
		}else {
			System.out.println("zle haslo      " );
			model.addAttribute("userError", "Password don't match");
			return new ModelAndView("signin");
		}
	}
}

