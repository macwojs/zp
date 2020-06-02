package edu.agh.zp.controller;

import edu.agh.zp.objects.ParliamentarianEntity;
import edu.agh.zp.repositories.ParliamentarianRepository;

import edu.agh.zp.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;



@Controller
@RequestMapping(value = {"/funkcyjni"})
public class FunkcyjniController {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ParliamentarianRepository parliamentarianRepository;

    @GetMapping(value = {""})
    public ModelAndView index(){
        ModelAndView model = new ModelAndView();
        model.addObject("sejm", parliamentarianRepository.findAllByChamberOfDeputies("Sejm"));
//        ParliamentarianEntity v = new ParliamentarianEntity();
//        v.getPoliticalGroup();
        model.addObject("senat", parliamentarianRepository.findAllByChamberOfDeputies("Senat"));
        model.addObject("prezydent", roleRepository.findByName("ROLE_PREZYDENT").get().getUsers());
        model.addObject("marszalek_sejm",parliamentarianRepository.findByPoliticianID_CitizenIDIn(roleRepository.findByName("ROLE_MARSZALEK_SEJMU").get().getUsers()));
        model.addObject("marszalek_senat",parliamentarianRepository.findByPoliticianID_CitizenIDIn(roleRepository.findByName("ROLE_MARSZALEK_SENATU").get().getUsers()));
        model.setViewName("funkcyjni");
        return model;
    }

}
