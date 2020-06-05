package edu.agh.zp.controller;

import edu.agh.zp.objects.DocumentEntity;
import edu.agh.zp.objects.DocumentStatusEntity;
import edu.agh.zp.objects.DocumentTypeEntity;
import edu.agh.zp.objects.VotingEntity;
import edu.agh.zp.repositories.DocumentRepository;
import edu.agh.zp.repositories.DocumentStatusRepository;
import edu.agh.zp.repositories.DocumentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import java.sql.Date;
import javax.servlet.http.HttpServletRequest;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


@Controller
@RequestMapping ( value = { "/ustawy" } )
public class UstawyController {

	@Autowired
	DocumentRepository documentRepository;
	@Autowired
	DocumentTypeRepository docTypeR;

	@Autowired
	DocumentStatusRepository documentStatusRepository;

	@GetMapping ( value = { "" } )
	public RedirectView index() {
		RedirectView redirect = new RedirectView( );
		redirect.setUrl("ustawy/dziennikUstaw/");
		return redirect;
	}

	@GetMapping ( "/{id}" )
	public ModelAndView index( @PathVariable Long id ) {
		Optional<DocumentEntity> document = documentRepository.findByDocID(id);
		if (document.isEmpty()){
			return new ModelAndView( String.valueOf( HttpStatus.NOT_FOUND ) );
		}
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "ustawaPodglad" );
		modelAndView.addObject("id",id);
		modelAndView.addObject( "doc", document.get() );
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
		List<DocumentStatusEntity> statuses = new ArrayList<DocumentStatusEntity>();
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
				statuses = documentStatusRepository.findByDocStatusNameIn(Collections.singletonList("Do ponownego rozpatrzenia w Sejmie: Senat"));
			case "Do ponownego rozpatrzenia w Sejmie: Senat":
				statuses.addAll(documentStatusRepository.findByDocStatusNameIn(Arrays.asList("Do zatwierdzenia przez Prezydenta", "Odrzucona")));
				break;
			case "Do zatwierdzenia przez Prezydenta":
				statuses =documentStatusRepository.findByDocStatusNameIn(Arrays.asList("Przyjęta", "Do ponownego rozpatrzenia w Sejmie: Prezydent"));
				break;
			case "Przyjęta":
				statuses = documentStatusRepository.findByDocStatusNameIn(Collections.singletonList("Wygasła"));
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
		if (type != 11) documentRepository.UpdateStatusByID(id,type);
		else documentRepository.ActivateStatusByID(id,type);
		RedirectView redirect = new RedirectView( );
		redirect.setUrl("/ustawy/"+id);
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
		setOptionsList(modelAndView);
		modelAndView.setViewName( "documentList" );
		return modelAndView;
	}

	@GetMapping(value = {"/description/{id}"})
	public ModelAndView descriptionEdit(ModelAndView model, @PathVariable long id){
		Optional<DocumentEntity> document = documentRepository.findByDocID(id);
		if (document.isEmpty()){
			model.setViewName("404 NOT_FOUND");
			return model;
		}
		String docDesc = document.get().getDocDescription();

		model.addObject("currentDesc",docDesc);
		model.addObject("id",id);
		model.setViewName("descEdit");
		return model;
	}

	@PostMapping(value = {"/description/{id}"})
	public RedirectView descriptionEditPost(@PathVariable long id, @RequestParam("desc") String desc ) {
		DocumentEntity doc = documentRepository.getOne( id );
		doc.setDocDescription( desc );
		documentRepository.save( doc );

		RedirectView redirect = new RedirectView( );
		redirect.setUrl("/ustawy/"+id);
		return redirect;
	}

	@GetMapping ( value = { "/dziennikUstaw/filtr" } )
	public ModelAndView documentFilteredList( HttpServletRequest request,
											  @RequestParam(name="docType", required = false) Long docType,
											  @RequestParam(name="docStatus", required = false) Long docStatus,
											  @RequestParam(name="dateControl", required = false) Long dateControl,
											  @RequestParam(name="date", required = false) String date) throws Exception {
		ModelAndView modelAndView = new ModelAndView( );

		int page = 0;
		int size = 10;
		if ( request.getParameter( "page" ) != null && !request.getParameter( "page" ).isEmpty( ) ) {
			page = Integer.parseInt( request.getParameter( "page" ) ) - 1;
		}
		if ( request.getParameter( "size" ) != null && !request.getParameter( "size" ).isEmpty( ) ) {
			size = Integer.parseInt( request.getParameter( "size" ) );
		}
		Page< DocumentEntity > documents;

		List<DocumentStatusEntity> docStatuses =
				(docStatus == 0 ) ? documentStatusRepository.findAll() : Arrays.asList(documentStatusRepository.findByDocStatusID(docStatus));
		List<DocumentTypeEntity> docTypes = (docType == 0 ) ? docTypeR.findAll() : Arrays.asList(docTypeR.findByDocTypeID(docType));

		if(date.isEmpty()) {
			documents = documentRepository.findAllByDocStatusIDInAndDocTypeIDIn(docStatuses, docTypes, PageRequest.of(page, size));
		}else{
			Date temp = Date.valueOf(date);
			if(dateControl == 1){
				documents = documentRepository.findAllByStatusAndTypeAfter( docStatuses, docTypes, temp, PageRequest.of(page, size));
			}else{
				documents =documentRepository.findAllByStatusAndTypeBefore(docStatuses, docTypes, temp, PageRequest.of(page, size));
			}
		}

		setOptionsList(modelAndView);
		setSelected(modelAndView, docType, docStatus, dateControl, date);
		modelAndView.addObject( "documents", documents );
		modelAndView.setViewName( "documentList" );
		return modelAndView;
	}

	private void setSelected(ModelAndView modelAndView, Long docType, Long docStatus, Long dateControl, String date){
		modelAndView.addObject("selectedType", docType);
		modelAndView.addObject("selectedStatus", docStatus);
		modelAndView.addObject("selectedDate", date);
		modelAndView.addObject("selectedDateControl", dateControl);
	}

	private void setOptionsList(ModelAndView modelAndView){
		modelAndView.addObject("documentTypes", docTypeR.findAll());
		modelAndView.addObject("documentStatus",
				documentStatusRepository.findByDocStatusNameIn(Arrays.asList("Przyjęta","Odrzucona")));
		modelAndView.addObject("legislativeStage",
				documentStatusRepository.findByDocStatusNameIn(Arrays.asList(
						"Zgłoszona",
						"Głosowanie w Sejmie",
						"Głosowanie w Senacie",
						"Do zatwierdzenia przez Prezydenta",
						"Do ponownego rozpatrzenia w Sejmie: Senat",
						"Do ponownego rozpatrzenia w Sejmie: Prezydent"
				))
		);
	}

}
