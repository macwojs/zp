package edu.agh.zp.controller;

import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.*;
import edu.agh.zp.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping(value = {"/parlament"})
public class ParlamentController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private DocumentStatusRepository documentStatusRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private VotingRepository votingRepository;


    @GetMapping(value = {""})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("parlament");
        return modelAndView;
    }


    @GetMapping(value = {"/ustawy"})
    public ModelAndView ustawy() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("ustawy");
        return modelAndView;
    }

    @GetMapping(value = {"/documentForm"})
    public ModelAndView documentForm(ModelAndView model) {
        List<DocumentTypeEntity> types = documentTypeRepository.findAll();
        model.addObject("types", types);

        List<DocumentStatusEntity> statuses = documentStatusRepository.findAll();
        model.addObject("statuses", statuses);

        model.addObject("document", new DocumentEntity());

        model.setViewName("documentForm");
        return model;
    }

    @PostMapping(value = {"/documentForm"}, consumes = {"multipart/form-data"})
    public ModelAndView documentFormSubmit(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, @Valid @ModelAttribute("document") DocumentEntity document, BindingResult res) {
        if (res.hasErrors()) {
            return new ModelAndView("documentForm");
        }

        if (!file.isEmpty()) {
            String path = storageService.uploadFile(file);
            document.setPdfFilePath(path);
        }

        documentRepository.save(document);

        RedirectView redirect = new RedirectView();
        redirect.setUrl("/parlament");
        return new ModelAndView(redirect);
    }

    @GetMapping(value = {"/vote/{id}"})
    public ModelAndView parlamentVote(ModelAndView model, @PathVariable long id) {
        VoteEntity vote = new VoteEntity();
        VotingEntity voting = votingRepository.findByVotingID(id);
        vote.setVotingID(voting);
        //TODO Sprawdź, czy już nie głosował!!!
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        vote.setCitizenID(citizenRepository.findByEmail(auth.getName()).get());
        model.addObject("vote", vote);
        model.addObject("voting",voting);
//	    for (OptionSetEntity optionSet :optionSetRepository.findBySetIDcolumn(setRepository.findById(1L).get()))
//		{
//			TODO Nie usuwać, ta idea będzie użyta w wyborach  prezydenckich!
//		}
        model.addObject("options", new ArrayList<OptionEntity>(Arrays.asList(
                optionRepository.findById(1L).get(),
                optionRepository.findById(2L).get(),
                optionRepository.findById(3L).get())));
        model.setViewName("parliamentVoting");
        return model;
    }

    @PostMapping(value = {"/vote/add"})
    public ModelAndView parlamentVoteSubmit(@ModelAttribute("vote") VoteEntity vote, @ModelAttribute("options") ArrayList<OptionEntity> options, BindingResult res) {
        LocalTime time = LocalTime.now();
        LocalDate date = LocalDate.now();
        VotingEntity voting = vote.getVotingID();
        if (voting.getCloseVoting().before(java.sql.Time.valueOf(time)) || !voting.getVotingDate().equals(java.sql.Date.valueOf(date))) {
            ModelAndView model = new ModelAndView();
            model.setViewName("timeOut");
            if (voting.getVotingType().equals(VotingEntity.TypeOfVoting.SEJM))
                model.addObject("type", "/parlament/sejm");
            else model.addObject("type", "/parlament/senat");
            return model;
        }
        vote.setVoteTimestamp(new Timestamp(System.currentTimeMillis()));
        voteRepository.save(vote);
        RedirectView redirect = new RedirectView();
        if (voting.getVotingType().equals(VotingEntity.TypeOfVoting.SEJM)) redirect.setUrl("/parlament/sejm");
        else redirect.setUrl("/parlament/senat");
        return new ModelAndView(redirect);
    }

}


