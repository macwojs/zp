package edu.agh.zp.controller;

import edu.agh.zp.objects.CitizenEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
//@RequestMapping (value={"/signin"})
public class SignInController {




	private AuthenticationManager authManager;

	public SignInController(AuthenticationManager authManager) {
		this.authManager = authManager;
	}

	// API

	// custom login
	@GetMapping (value = {"/signin"})
	public ModelAndView index() {

		String viewName = "signin" ;
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", new CitizenEntity());

		return new ModelAndView(viewName, model);
	}

	@RequestMapping(value = "/signin", method = RequestMethod.POST)
	public ModelAndView login(@ModelAttribute("user") CitizenEntity citizen,   Model model, final HttpServletRequest request) {
		UsernamePasswordAuthenticationToken authReq =
				new UsernamePasswordAuthenticationToken(citizen.getEmail(), citizen.getPassword());
		Authentication auth = authManager.authenticate(authReq);
		SecurityContext sc = SecurityContextHolder.getContext();
		sc.setAuthentication(auth);
		HttpSession session = request.getSession(true);
		session.setAttribute("SPRING_SECURITY_CONTEXT", sc);

		return new ModelAndView("index");
	}


//	private CitizenService cS;
//	public SignInController(CitizenService cS){
//		this.cS = cS;
//	}
//	//private ApplicationContext context;
//
//	@GetMapping (value = {""})
//	public ModelAndView index() {
//		String viewName = "signin" ;
//		Map<String, Object> model = new HashMap<String, Object>();
//		model.put("user", new CitizenEntity());
//		return new ModelAndView(viewName, model);
//	}
//
//	@PostMapping("")
//	public ModelAndView submitLogin(@ModelAttribute("user") CitizenEntity citizen,  Model model, HttpSession session){
//		Optional<CitizenEntity> c = cS.findByEmail(citizen.getEmail());
//		//session.invalidate();
//		System.out.println("\n\n\njestem\n\n\n" );
//		System.out.println("\n\n\n"+citizen.getEmail()+"   "+citizen.getPassword()+"\n\n\n" );
//		if(c.isEmpty()){
//			System.out.println("\n\n\nzly mail\n\n\n" );
//			model.addAttribute("userError","A user with this email don't already exist");
//			return new ModelAndView("signin");
//		}else if(BCrypt.checkpw(citizen.getPassword(),c.get().getPassword())) {
//			System.out.println("\n\n\nzalogowany\n\n\n" );
//			session.setAttribute("user",c.get().getCitizenID());
//			RedirectView redirect = new RedirectView();
//			redirect.setUrl("");
//			System.out.println("\n\n\n"+session.getAttribute("user")+"\n\n\n" );
//			return new ModelAndView(redirect);
//		}else {
//			System.out.println("\n\n\nzle haslo \n\n\n" );
//			model.addAttribute("userError", "Password don't match");
//			return new ModelAndView("signin");
//		}
	}


