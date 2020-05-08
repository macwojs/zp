package edu.agh.zp.controller;

import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.OptionRepository;
import edu.agh.zp.repositories.OptionSetRepository;
import edu.agh.zp.repositories.SetRepository;
import edu.agh.zp.repositories.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping(value = {"/glosowania"})
public class GlosowaniaController {

    @Autowired
    VotingRepository votingSession;

    @Autowired
    OptionSetRepository optionSetSession;

    @Autowired
    OptionRepository optionSession;

    @Autowired
    SetRepository setSession;

    @GetMapping(value = {""})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("glosowania");
        return modelAndView;
    }

    @GetMapping(value = {"/prezydenckie/plan"})
    public ModelAndView prezydentForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("ErrorList", null);
        modelAndView.setViewName("presidentVotingAdd");
        return modelAndView;
    }

    @PostMapping(value = {"/prezydenckie/planAdd"})
    public ModelAndView prezydentSubmit(@RequestParam Map<String, String> reqParameters) throws ParseException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("glosowania");
        String data = reqParameters.remove("date");
        SetEntity set = new SetEntity("Wybory Prezydenckie " + data);
        LocalDate time = timeVerify(data, 7);
        if (time == null || reqParameters.size() < 3 || reqParameters.containsValue("")) {
            ArrayList<String> errors = errorsMsg(time, 7, reqParameters);
            modelAndView.setViewName("presidentVotingAdd");
            modelAndView.addObject("ErrorList", errors);
            return modelAndView;
        }
        setSession.save(set);
        votingSession.save(new VotingEntity(
                java.sql.Date.valueOf(time),
                java.sql.Time.valueOf(LocalTime.parse("06:00:00")),
                java.sql.Time.valueOf(LocalTime.parse("21:00:00")),
                set,
                null,
                VotingEntity.TypeOfVoting.PREZYDENT,
                "Wybory Prezydenckie " + data));
        for (Map.Entry<String, String> entry : reqParameters.entrySet()) {
            if (entry.getKey().equals("_csrf")) continue;
            OptionEntity option = new OptionEntity(entry.getValue());
            optionSession.save(option);
            optionSetSession.save(new OptionSetEntity(option, set));
        }
        return modelAndView;
    }

    @GetMapping(value = {"/referendum/plan"})
    public ModelAndView referendumForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("voting", new VotingEntity());
        modelAndView.setViewName("referendumAdd");
        return modelAndView;
    }

    @PostMapping(value = {"/referendum/planAdd"})
    public ModelAndView referendumSubmit(@Valid @ModelAttribute( "voting" ) VotingEntity voting, BindingResult res ) throws ParseException {
        if (res.hasErrors()) {
            for (Object i : res.getAllErrors()) {
                System.out.print("\n" + i.toString() + "\n");
            }
            ModelAndView model = new ModelAndView();
            model.setViewName("referendumAdd");
            return model;
        }
        Optional<SetEntity> set = setSession.findById((long) 2);
        if (set.isPresent()) {
            voting.setSetID_column(set.get());
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            Time timeValueOpen = new Time(formatter.parse(voting.getOpen()).getTime());
            Time timeValueClose = new Time(formatter.parse(voting.getClose()).getTime());
            voting.setCloseVoting(timeValueClose);
            voting.setOpenVoting(timeValueOpen);
            voting.setVotingType(VotingEntity.TypeOfVoting.REFERENDUM);
            votingSession.save(voting);
        }
        RedirectView redirect = new RedirectView();
        redirect.setUrl("/parlament/sejm");
        return new ModelAndView(redirect);
    }


    public LocalDate timeVerify(String time, int delay) {
        if (time.isEmpty()) return null;
        LocalDate now = java.time.LocalDate.now();
        now = now.plusDays(delay);
        LocalDate res = LocalDate.parse(time);
        if (now.isAfter(res)) return null;
        return res;
    }

    public ArrayList<String> errorsMsg(LocalDate date, int delay, Map<String, String> param) {
        ArrayList<String> res = new ArrayList<String>();
        if (date == null) {
            res.add("wydarzenie musi być zaplanowane z " + delay + " dniowym wyprzedzeniem\n");
        }
        if (param.size() < 3) {
            res.add("musisz podać przynajmnej 2 kandydatów\n");
        }
        if (param.containsValue("")) {
            res.add("dane kandydata nie mogą być puste\n");
        }
        return res;
    }

}

