package edu.agh.zp.controller;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.services.CitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
//@RequestMapping (value={"/signin"})
public class SignInController {

    @Autowired
    private CitizenService cS;


    private final AuthenticationManager authManager;

    public SignInController(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    // API

    // custom login
    @GetMapping(value = {"/signin"})
    public ModelAndView index(RedirectAttributes attributes, HttpServletRequest request, HttpServletResponse response) {

        String viewName = "signin";
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", new CitizenEntity());

        String referrer = request.getHeader("Referer");
        if (referrer != null) {
            model.put("url_prior_login", referrer);
        }
        response.addCookie(new Cookie("OLD_URL_REDIRECT", request.getHeader("Referer")));
        return new ModelAndView(viewName, model);
    }

    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public RedirectView login(@ModelAttribute("user") CitizenEntity citizen, Model model, final HttpServletRequest request, BindingResult res, @CookieValue(name = "OLD_URL_REDIRECT") String ref) {
        UsernamePasswordAuthenticationToken authReq =
                new UsernamePasswordAuthenticationToken(citizen.getEmail(), citizen.getPassword());
        Authentication auth = null;
        try {
            auth = authManager.authenticate(authReq);
        } catch (AuthenticationException e) {
            RedirectView mv = new RedirectView("/signin");
            mv.addStaticAttribute("error", "Błędna nazwa użytkownika lub hasło");
            return mv;
        }
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", sc);
        if (ref != null) return new RedirectView(ref);
        return new RedirectView("");
    }

    @GetMapping(value = {"/logout"})
    public ModelAndView logout(final HttpServletRequest request) throws ServletException {
        request.logout();
        RedirectView redirect = new RedirectView();
        redirect.setUrl("");
        return new ModelAndView(redirect);
    }

}


