package edu.agh.zp.controller;

import edu.agh.zp.objects.User;
import edu.agh.zp.services.CitizenService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping (value={"/signin"})
public class SignInController {
	private CitizenService cS;
	private ApplicationContext context;

	@GetMapping (value = {""})
	public ModelAndView index() {
		String viewName = "signin" ;
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", new User());
		return new ModelAndView(viewName, model);
	}

	@PostMapping("")
	public ModelAndView submitLogin(@ModelAttribute("user") User user, BindingResult res){
		if( res.hasErrors()){

		}else{
			//cS =  new CitizenService(context.getBean(CitizenRepository.class));
		}
		RedirectView redirect = new RedirectView();
		redirect.setUrl("");
		return new ModelAndView(redirect);
	}
}
