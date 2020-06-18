package edu.agh.zp.controller;

import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.DocumentRepository;
import edu.agh.zp.repositories.DocumentStatusRepository;
import edu.agh.zp.repositories.DocumentTypeRepository;
import edu.agh.zp.repositories.LogRepository;
import edu.agh.zp.services.CitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import java.sql.Date;
import javax.servlet.http.HttpServletRequest;
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

	@Autowired
	CitizenService cS;

	@Autowired
	LogRepository logR;

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

	@GetMapping ( "/prezydent" )
	public ModelAndView documentListToSign( HttpServletRequest request,
									  @RequestParam(name="docType", required = false, defaultValue = "0") Long docType,
									  @RequestParam(name="dateControl", required = false, defaultValue = "") Long dateControl,
									  @RequestParam(name="date", required = false, defaultValue ="") String date)  {
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
				documentStatusRepository.findByDocStatusNameIn(Collections.singletonList(
						"Do zatwierdzenia przez Prezydenta"
				));
		List<DocumentTypeEntity> docTypes = (docType == 0 ) ? docTypeR.findAll() : Collections.singletonList(docTypeR.findByDocTypeID(docType));

		if(date.isEmpty()) {
			documents = documentRepository.findAllByDocStatusIDInAndDocTypeIDIn(docStatuses, docTypes, PageRequest.of(page, size));
		}else{
			Date temp = Date.valueOf(date);
			if(dateControl == 1){
				documents = documentRepository.findAllByStatusAndTypeLastEditAfter( docStatuses, docTypes, temp, PageRequest.of(page, size));
			}else{
				documents =documentRepository.findAllByStatusAndTypeLastEditBefore(docStatuses, docTypes, temp, PageRequest.of(page, size));
			}
		}

		modelAndView.addObject( "documents", documents );
		setOptionsListForDocumentListInProgress(modelAndView);
		setSelected(modelAndView, docType, null, dateControl, date);
		modelAndView.setViewName( "documentToSign" );
		return modelAndView;
	}

	@GetMapping ( "/prezydent/{fun}/{id}" )
	public RedirectView Signin(HttpServletRequest request,@PathVariable long id, @PathVariable int fun  ) throws Exception {
		CitizenEntity citizen = cS.findByEmail(request.getRemoteUser()).orElseThrow(() -> new Exception("Citizen not found"));
		Optional<DocumentEntity> doc = documentRepository.findByDocID(id);
		if( doc.isEmpty()){
			logR.save(new Log(Log.Operation.EDIT, "Failed to edit document status - document not found", Log.ElementType.DOCUMENT, citizen, Log.Status.FAILURE));
			throw new Exception("Document not found");
		}
		if (fun==1) documentRepository.UpdateStatusByID(id,3);
		else documentRepository.ActivateStatusByID(id,9);
		logR.save(new Log(Log.Operation.EDIT, "Edit document status successfully", Log.ElementType.DOCUMENT, doc.get().getDocID(), citizen, Log.Status.SUCCESS));
		RedirectView redirect = new RedirectView( );
		redirect.setUrl("/ustawy/prezydent");
		return redirect;
	}

	@GetMapping(value = {"/status/{id}"})
	public ModelAndView statusList(ModelAndView model, @PathVariable long id){
		Optional<DocumentEntity> document = documentRepository.findByDocID(id);
		if (document.isEmpty()){
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Document not found" );
		}
		DocumentStatusEntity docStatus = document.get().getDocStatusID();
		model.addObject("currentStatus",docStatus);
		model.addObject("id",id);
		String name = docStatus.getDocStatusName();
		List<DocumentStatusEntity> statuses = new ArrayList<>();
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
	public RedirectView statusListAdd(@PathVariable long id, @RequestParam("type") long type,  final HttpServletRequest request  ) throws Exception {
		CitizenEntity citizen = cS.findByEmail(request.getRemoteUser()).orElseThrow(() -> new Exception("Citizen not found"));
		Optional<DocumentEntity> doc = documentRepository.findByDocID(id);
		if( doc.isEmpty()){
			logR.save(new Log(Log.Operation.EDIT, "Failed to edit document status - document not found", Log.ElementType.DOCUMENT, citizen, Log.Status.FAILURE));
			throw new Exception("Document not found");
		}

		if (type != 11) documentRepository.UpdateStatusByID(id,type);
		else documentRepository.ActivateStatusByID(id,type);
		logR.save(new Log(Log.Operation.EDIT, "Edit document status successfully", Log.ElementType.DOCUMENT, doc.get().getDocID(), citizen, Log.Status.SUCCESS));
		RedirectView redirect = new RedirectView( );
		redirect.setUrl("/ustawy/"+id);
		return redirect;
	}

	@GetMapping(value = {"/description/{id}"})
	public ModelAndView descriptionEdit(ModelAndView model, @PathVariable long id,  final HttpServletRequest request ) throws Exception {
		Optional<DocumentEntity> document = documentRepository.findByDocID(id);
		CitizenEntity citizen = cS.findByEmail(request.getRemoteUser()).orElseThrow(()->new Exception("Citizen not found"));
		if (document.isEmpty()){
			logR.save(new Log(Log.Operation.EDIT, "Edit document description failure - document not found", Log.ElementType.DOCUMENT, citizen, Log.Status.FAILURE));
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Document not found" );
		}
		String docDesc = document.get().getDocDescription();

		model.addObject("currentDesc",docDesc);
		model.addObject("id",id);
		model.setViewName("descEdit");
		return model;
	}

	@PostMapping(value = {"/description/{id}"})
	public RedirectView descriptionEditPost(@PathVariable long id, @RequestParam("desc") String desc,  final HttpServletRequest request ) throws Exception {
		DocumentEntity doc = documentRepository.getOne( id );
		CitizenEntity citizen = cS.findByEmail(request.getRemoteUser()).orElseThrow(() -> new Exception("Citizen not found"));

		doc.setDocDescription( desc );
		documentRepository.save( doc );
		logR.save(new Log(Log.Operation.EDIT, "Edit document description successfully", Log.ElementType.DOCUMENT, doc.getDocID(), citizen, Log.Status.SUCCESS));
		RedirectView redirect = new RedirectView( );
		redirect.setUrl("/ustawy/"+id);
		return redirect;
	}

	@GetMapping ( value = { "/dziennikUstaw" } )
	public ModelAndView documentList( HttpServletRequest request,
									  @RequestParam(name="docType", required = false, defaultValue = "0") Long docType,
									  @RequestParam(name="docStatus", required = false, defaultValue = "0") Long docStatus,
									  @RequestParam(name="dateControl", required = false, defaultValue = "") Long dateControl,
									  @RequestParam(name="date", required = false, defaultValue ="") String date)  {
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
				(docStatus == 0) ? documentStatusRepository.findAllByDocStatusIDIn(Arrays.asList((long)3, (long)11)) : Collections.singletonList(documentStatusRepository.findByDocStatusID(docStatus));
		List<DocumentTypeEntity> docTypes = (docType == 0 ) ? docTypeR.findAll() : Collections.singletonList(docTypeR.findByDocTypeID(docType));

		if(date.isEmpty() ) {
			documents = documentRepository.findAllByDocStatusIDInAndDocTypeIDIn(docStatuses, docTypes, PageRequest.of(page, size));
		}else{
			Date temp = Date.valueOf(date);
			if(dateControl == 1){
				documents = documentRepository.findAllByStatusAndTypeAfter( docStatuses, docTypes, temp, PageRequest.of(page, size));
			}else{
				documents =documentRepository.findAllByStatusAndTypeBefore(docStatuses, docTypes, temp, PageRequest.of(page, size));
			}
		}


		modelAndView.addObject( "documents", documents );
		setOptionsListForDocumentList(modelAndView);
		setSelected(modelAndView, docType, docStatus, dateControl, date);
		modelAndView.setViewName( "documentList" );
		return modelAndView;
	}

	@GetMapping ( value = { "/listaUstaw" } )
	public ModelAndView documentListInProgress( HttpServletRequest request,
												@RequestParam(name="docType", required = false, defaultValue = "0") Long docType,
												@RequestParam(name="docStatus", required = false, defaultValue = "0") Long docStatus,
												@RequestParam(name="dateControl", required = false, defaultValue = "") Long dateControl,
												@RequestParam(name="date", required = false, defaultValue = "") String date)  {
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

		List<DocumentStatusEntity> docStatuses = (docStatus == 0 ) ?
						documentStatusRepository.findByDocStatusNameIn(Arrays.asList(
						"Zgłoszona",
						"Głosowanie w Sejmie",
						"Głosowanie w Senacie",
						"Do zatwierdzenia przez Prezydenta",
						"Do ponownego rozpatrzenia w Sejmie: Senat",
						"Do ponownego rozpatrzenia w Sejmie: Prezydent",
						"Odrzucona"
				)) : Collections.singletonList(documentStatusRepository.findByDocStatusID(docStatus));
		List<DocumentTypeEntity> docTypes = (docType == 0 ) ? docTypeR.findAll() : Collections.singletonList(docTypeR.findByDocTypeID(docType));

		if(date.isEmpty()) {
			documents = documentRepository.findAllByDocStatusIDInAndDocTypeIDIn(docStatuses, docTypes, PageRequest.of(page, size));
		}else{
			Date temp = Date.valueOf(date);
			if(dateControl == 1){
				documents = documentRepository.findAllByStatusAndTypeLastEditAfter( docStatuses, docTypes, temp, PageRequest.of(page, size));
			}else{
				documents =documentRepository.findAllByStatusAndTypeLastEditBefore(docStatuses, docTypes, temp, PageRequest.of(page, size));
			}
		}

		modelAndView.addObject( "documents", documents );
		setOptionsListForDocumentListInProgress(modelAndView);
		setSelected(modelAndView, docType, docStatus, dateControl, date);
		modelAndView.setViewName( "documentList" );
		return modelAndView;
	}

	private void setSelected(ModelAndView modelAndView, Long docType, Long docStatus, Long dateControl, String date){
		modelAndView.addObject("selectedType", docType);
		modelAndView.addObject("selectedStatus", docStatus);
		modelAndView.addObject("selectedDate", date);
		modelAndView.addObject("selectedDateControl", dateControl);
	}

	private void setOptionsListForDocumentList(ModelAndView modelAndView){
		modelAndView.addObject("documentTypes", docTypeR.findAll());
		modelAndView.addObject("documentStatus",
				documentStatusRepository.findByDocStatusNameIn(Arrays.asList("Przyjęta","Wygasła")));
	}

	private void setOptionsListForDocumentListInProgress(ModelAndView modelAndView) {
		modelAndView.addObject("documentTypes", docTypeR.findAll());
		modelAndView.addObject("documentStatus",
				documentStatusRepository.findByDocStatusNameIn(Arrays.asList(
						"Zgłoszona",
						"Głosowanie w Sejmie",
						"Głosowanie w Senacie",
						"Do zatwierdzenia przez Prezydenta",
						"Do ponownego rozpatrzenia w Sejmie: Senat",
						"Do ponownego rozpatrzenia w Sejmie: Prezydent",
						"Odrzucona"
				))
		);
	}
}
