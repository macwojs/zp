package edu.agh.zp.controller;

import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Date;
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

    @Autowired
    VotingTimerRepository votingTimerSession;

    @Autowired
    VotingControlRepository votingControlSession;

    @Autowired
    CitizenRepository citizenSession;

    @Autowired
    VoteRepository voteSession;



    @GetMapping(value = {""})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        createVotingList.run( modelAndView, VotingEntity.TypeOfVoting.REFERENDUM, votingSession );
        modelAndView.setViewName("glosowania");
        return modelAndView;
    }

    @GetMapping(value = {"/prezydenckie/plan"})
    public ModelAndView prezydentForm() {
        deleteOldVotingData(Date.valueOf(LocalDate.now()));
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
        VotingEntity voting = new VotingEntity(
                java.sql.Date.valueOf(time),
                java.sql.Time.valueOf(LocalTime.parse("06:00:00")),
                java.sql.Time.valueOf(LocalTime.parse("21:00:00")),
                set,
                null,
                VotingEntity.TypeOfVoting.PREZYDENT,
                "Wybory Prezydenckie " + data);
        votingSession.save(voting);
        votingTimerSession.save(new VotingTimerEntity(voting.getVotingID(),java.sql.Date.valueOf(time)));
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
        deleteOldVotingData(Date.valueOf(LocalDate.now()));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("ErrorList", null);
        modelAndView.setViewName("referendumAdd");
        return modelAndView;
    }

    @PostMapping(value = {"/referendum/planAdd"})
    public ModelAndView referendumSubmit(@RequestParam Map<String, String> reqParameters) throws ParseException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("glosowania");
        String data = reqParameters.remove("date");
        LocalDate time = timeVerify(data, 7);
        String desc = reqParameters.remove("desc");
        if (time == null || desc.isEmpty()) {
            ArrayList<String>  errors = new ArrayList<String>();
            if (time==null) errors.add("Wydarzenie musi być zaplanowane z 7 dniowym wyprzedzeniem\n");
            if (desc.isEmpty()) errors.add("Należy wpisać treść pytania\n");
            modelAndView.setViewName("referendumAdd");
            modelAndView.addObject("ErrorList", errors);
            return modelAndView;
        }
        VotingEntity voting = new VotingEntity(
                java.sql.Date.valueOf(time),
                java.sql.Time.valueOf(LocalTime.parse("06:00:00")),
                java.sql.Time.valueOf(LocalTime.parse("21:00:00")),
                setSession.findById(2L).get(),
                null,
                VotingEntity.TypeOfVoting.REFERENDUM,
                desc);
        votingSession.save(voting);
        votingTimerSession.save(new VotingTimerEntity(voting.getVotingID(),java.sql.Date.valueOf(time)));
        return modelAndView;
    }

    @GetMapping(value={"{id}"})
    public ModelAndView referendumVote(ModelAndView model, @PathVariable long id, Principal principal) {
        Optional< CitizenEntity > optCurUser = citizenSession.findByEmail( principal.getName( ) );
        if(optCurUser.isEmpty()){
            model.setViewName("signin");
            return model;
        }
        VotingEntity voting = votingSession.findByVotingID( id );
        Optional< VotingControlEntity > votingControl = votingControlSession.findByCitizenIDAndVotingID(optCurUser.get(),voting);
        if (votingControl.isPresent()) {
            model.addObject( "th_redirect", "/glosowania" );
            model.setViewName( "418_REPEAT_VOTE" );
            return model;
        }
        model.addObject( "voting", voting );
        model.addObject( "id", id );
        if(voting.getVotingType().equals(VotingEntity.TypeOfVoting.REFERENDUM)) model.setViewName( "referendumVoting" );
        else model.setViewName("presidentVoting");
        return model;
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

    public void deleteOldVotingData(Date time)
    {
        List<VotingTimerEntity> list = votingTimerSession.findByEraseBefore(time);
        if (list.isEmpty()) return;
        for (VotingTimerEntity Timer : list)
        {
            votingControlSession.deleteAllByVotingID(votingSession.findByVotingID(Timer.getVotingID()));
            votingTimerSession.delete(Timer);
        }
    }

}

