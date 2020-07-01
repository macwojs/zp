package edu.agh.zp.controller;

import edu.agh.zp.objects.DocumentEntity;
import edu.agh.zp.objects.DocumentStatusEntity;
import edu.agh.zp.objects.DocumentTypeEntity;
import edu.agh.zp.objects.VotingEntity;
import edu.agh.zp.repositories.DocumentRepository;
import edu.agh.zp.repositories.DocumentStatusRepository;
import edu.agh.zp.repositories.DocumentTypeRepository;
import edu.agh.zp.repositories.VotingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Date;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


@Controller
@RequestMapping(value={""})
public class IndexController {

	@Autowired
	private DocumentStatusRepository documentStatusRepository;
	@Autowired
	private DocumentTypeRepository documentTypeRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private VotingRepository votingRepository;

	@GetMapping(value = {""})
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );

		//Obsługa ustaw
		Page< DocumentEntity > documents;
		List< DocumentStatusEntity > docStatuses = documentStatusRepository.findAllByDocStatusIDIn( Collections.singletonList( (long) 3 ) );
		List< DocumentTypeEntity > docTypes = documentTypeRepository.findAll();
		documents = documentRepository.findAllByDocStatusIDInAndDocTypeIDIn(docStatuses, docTypes, PageRequest.of(0, 10, Sort.by("lastEdit").descending()));
		modelAndView.addObject( "documents", documents );

		modelAndView.setViewName( "index" );
		return modelAndView;
	}
}
