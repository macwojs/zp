package edu.agh.zp.controller;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.Role;
import edu.agh.zp.repositories.RoleRepository;
import edu.agh.zp.services.CitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
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
import java.util.*;

@Controller
@RequestMapping (value={"/signup"})
public class SignUpController {
	private CitizenService cS;
	@Autowired
	private RoleRepository rR;
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
	public ModelAndView submitRegister(@Valid @ModelAttribute("user") CitizenEntity citizen, BindingResult res){
		if( res.hasErrors()){
			return new ModelAndView("signup");
		}else{
			Optional<CitizenEntity> exists = cS.findByEmail(citizen.getEmail());
			if(exists.isPresent()){
				ModelAndView mv = new ModelAndView("signup");
				mv.addObject("error", "Obywetel z tym adresem e-mail już istnieje.");
				return mv;
			}
			exists = cS.findByPesel(citizen.getPesel());
			if(exists.isPresent()){
				ModelAndView mv = new ModelAndView("signup");
				mv.addObject("error", "Obywetel z tym numerem PESEL już istnieje.");
				return mv;
			}
			exists = cS.findByIdNumer(citizen.getIdNumber());
			if(exists.isPresent()){
				ModelAndView mv = new ModelAndView("signup");
				mv.addObject("error", "Obywatel z tym numerem dowodu osobistego już istnieje.");
				return mv;
			} else {
				citizen.setPassword(BCrypt.hashpw(citizen.getPassword(), BCrypt.gensalt()));
				List<Role> roles = new ArrayList <Role>();

				Optional<Role> temp = rR.findById((long)1);
				temp.ifPresent(roles::add);
				citizen.setRoles(roles);
				cS.create(citizen);
			}
		}
		RedirectView redirect = new RedirectView();
		redirect.setUrl("");
		return new ModelAndView(redirect);
	}
}
