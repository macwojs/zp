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
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
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

	@GetMapping (value = {"/logout"})
	public ModelAndView logout(final HttpServletRequest request) throws ServletException {
		request.logout();
		RedirectView redirect = new RedirectView();
		redirect.setUrl("");
		return new ModelAndView(redirect);
	}

	}


