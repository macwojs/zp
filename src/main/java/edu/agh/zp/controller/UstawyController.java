package edu.agh.zp.controller;

import edu.agh.zp.objects.DocumentEntity;
import edu.agh.zp.objects.DocumentStatusEntity;
import edu.agh.zp.repositories.DocumentRepository;
import edu.agh.zp.repositories.DocumentStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping ( value = { "/ustawy" } )
public class UstawyController {

	@Autowired
	DocumentRepository documentRepository;

	@Autowired
	DocumentStatusRepository documentStatusRepository;

	@GetMapping ( value = { "" } )
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "ustawy" );
		return modelAndView;
	}

	@GetMapping(value = {"/status/{id}"})
	public ModelAndView statusList(ModelAndView model, @PathVariable long id){
		Optional<DocumentEntity> document = documentRepository.findByDocID(id);
		if (document.isEmpty()){
			model.setViewName("404 NOT_FOUND");
			return model;
		}
		DocumentStatusEntity docStatus = document.get().getDocStatusID();
		model.addObject("currentStatus",docStatus);
		model.addObject("id",id);
		String name = docStatus.getDocStatusName();
		List<DocumentStatusEntity> statuses = null;
		model.setViewName("alterStatus");
		switch (name) {
			case "Zgłoszona":
			case "Pierwsze czytanie":
			case "Drugie czytanie":
				statuses = documentStatusRepository.findByDocStatusNameIn(Arrays.asList("Pierwsze czytanie", "Drugie czytanie", "Głosowanie w Sejmie"));
				break;
			case "Do ponownego rozpatrzenia w Sejmie: Prezydent":
				statuses = documentStatusRepository.findByDocStatusNameIn(Arrays.asList("Przyjęta", "Odrzucona"));
				break;
			case "Głosowanie w Sejmie":
				statuses = documentStatusRepository.findByDocStatusNameIn(Arrays.asList("Głosowanie w Senacie", "Odrzucona"));
				break;
			case "Głosowanie w Senacie":
			case "Do ponownego rozpatrzenia w Sejmie: Senat":
				statuses = documentStatusRepository.findByDocStatusNameIn(Arrays.asList("Do zatwierdzenia przez Prezydenta", "Odrzucona"));
				break;
			case "Do zatwierdzenia przez Prezydenta":
				statuses = documentStatusRepository.findByDocStatusNameIn(Arrays.asList("Przyjęta", "Do ponownego rozpatrzenia w Sejmie: Senat"));
				break;
			default:
				model.setViewName("finalStatus");
				model.addObject("th_redirect","ustawy/"+id);
		}
		model.addObject("statuses",statuses);
		return model;
	}

	@PostMapping(value = {"/status/{id}"})
	public RedirectView statusListAdd(@PathVariable long id, @RequestParam("type") long type ) {
		documentRepository.UpdateStatusByID(id,type);
		RedirectView redirect = new RedirectView( );
		redirect.setUrl("ustawy/"+id);
		return redirect;
	}


	@GetMapping ( value = { "/dziennikUstaw" } )
	public ModelAndView documentList( HttpServletRequest request ) {
		ModelAndView modelAndView = new ModelAndView( );

		int page = 0;
		int size = 10;
		if ( request.getParameter( "page" ) != null && !request.getParameter( "page" ).isEmpty( ) ) {
			page = Integer.parseInt( request.getParameter( "page" ) ) - 1;
		}
		if ( request.getParameter( "size" ) != null && !request.getParameter( "size" ).isEmpty( ) ) {
			size = Integer.parseInt( request.getParameter( "size" ) );
		}
		Page< DocumentEntity > documents = documentRepository.findAll( PageRequest.of( page, size ) );
		modelAndView.addObject( "documents", documents );
		modelAndView.setViewName( "documentList" );
		return modelAndView;
	}
}
