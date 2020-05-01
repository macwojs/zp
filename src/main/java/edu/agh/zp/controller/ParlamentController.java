package edu.agh.zp.controller;

import edu.agh.zp.objects.DocumentEntity;
import edu.agh.zp.objects.DocumentTypeEntity;
import edu.agh.zp.repositories.DocumentTypeRepository;
import edu.agh.zp.services.CitizenService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.swing.text.Document;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping (value={"/parlament"})
public class ParlamentController {

	private DocumentTypeRepository documentTypeRepository;

	public ParlamentController( DocumentTypeRepository documentTypeRepository){
		this.documentTypeRepository = documentTypeRepository;
	}

	@GetMapping (value = {""})
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "parlament" );
		return modelAndView;
	}


	@GetMapping(value = {"/ustawy"})
	public ModelAndView ustawy() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "ustawy" );
		return modelAndView;
	}

	@GetMapping(value = {"/documentForm"})
	public String documentForm( Model model) {

		DocumentEntity documentForm = new DocumentEntity();
		model.addAttribute( "documentForm", documentForm );

		//List< DocumentTypeEntity > documentTypeEntitie = new ArrayList<>();
		//model.addAttribute("type", documentTypeEntitie);

		List<DocumentTypeEntity> types = documentTypeRepository.findAll();
		//model.addAttribute("type", documentTypeEntities);
		model.addAttribute( "types", types );

		//model.addAttribute( "name", "documentForm" );

		//ModelAndView modelAndView = new ModelAndView( );
		//modelAndView.setViewName( "documentForm" );

		return "documentForm";
	}
}
