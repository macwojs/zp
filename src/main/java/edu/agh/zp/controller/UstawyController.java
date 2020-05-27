package edu.agh.zp.controller;

import edu.agh.zp.objects.DocumentEntity;
import edu.agh.zp.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping (value={"/ustawy"})
public class UstawyController {

	@Autowired
	DocumentRepository documentRepository;

	@GetMapping (value = {""})
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "ustawy" );
		return modelAndView;
	}

	@GetMapping (value = {"/dziennikUstaw"})
	public ModelAndView documentList() {
		ModelAndView modelAndView = new ModelAndView( );
		List< DocumentEntity > documents = documentRepository.findAll();
		modelAndView.addObject( "documents", documents );
		modelAndView.setViewName( "documentList" );
		return modelAndView;
	}
}
