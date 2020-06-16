package edu.agh.zp.controller;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.ParliamentarianEntity;
import edu.agh.zp.repositories.CitizenRepository;
import edu.agh.zp.repositories.ParliamentarianRepository;

import edu.agh.zp.repositories.PoliticianRepository;
import edu.agh.zp.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
        model.addObject("senat", parliamentarianRepository.findAllByChamberOfDeputies("Senat"));
        model.addObject("prezydent", roleRepository.findByName("ROLE_PREZYDENT").get().getUsers().get(0));
        model.addObject("marszalek_sejm",parliamentarianRepository.findByPoliticianID_CitizenIDIn(roleRepository.findByName("ROLE_MARSZALEK_SEJMU").get().getUsers()).get(0));
        model.addObject("marszalek_senat",parliamentarianRepository.findByPoliticianID_CitizenIDIn(roleRepository.findByName("ROLE_MARSZALEK_SENATU").get().getUsers()).get(0));
        model.setViewName("funkcyjni");
        return model;
    }

    @GetMapping(value = {"/{id}"})
    public ModelAndView index(@PathVariable Long id){
        if (id == 0){
            ModelAndView model = new ModelAndView();
            model.addObject("president",roleRepository.findByName("ROLE_PREZYDENT").get().getUsers().get(0));
            model.addObject("fn","Prezydent");
            model.setViewName("funkcyjniSzczegoly");
            return model;
        }
        Optional<ParliamentarianEntity> a = parliamentarianRepository.findById(id);
        if ( a.isEmpty() ) {
            return new ModelAndView( "error/404" );
        }
        ModelAndView model = new ModelAndView();
        model.addObject("politician",a.get());
        List<CitizenEntity>b=new ArrayList<CitizenEntity>();
        b.add(a.get().getPoliticianID().getCitizenID());
        model.addObject("fn",roleRepository.findByUsersIsIn(b).toString());
        model.setViewName("funkcyjniSzczegoly");
        return model;
    }
}
