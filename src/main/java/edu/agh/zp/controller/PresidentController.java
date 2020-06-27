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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping ( value = { "/prezydent" } )
public class PresidentController {

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

	@GetMapping ( "podpisz" )
	public ModelAndView documentListToSign( HttpServletRequest request,
	                                        @RequestParam (name="docType", required = false, defaultValue = "0") Long docType,
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

		List< DocumentStatusEntity > docStatuses =
				documentStatusRepository.findByDocStatusNameIn( Collections.singletonList(
						"Do zatwierdzenia przez Prezydenta"
				));
		List< DocumentTypeEntity > docTypes = (docType == 0 ) ? docTypeR.findAll() : Collections.singletonList(docTypeR.findByDocTypeID(docType));

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
		modelAndView.setViewName("Documents/documentToSign.html");
		return modelAndView;
	}

	@GetMapping ( "/podpisz/{fun}/{id}" )
	public RedirectView Signin( HttpServletRequest request, @PathVariable long id, @PathVariable int fun  ) throws Exception {
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
		redirect.setUrl("/prezydent/podpisz");
		return redirect;
	}

	private void setSelected(ModelAndView modelAndView, Long docType, Long docStatus, Long dateControl, String date){
		modelAndView.addObject("selectedType", docType);
		modelAndView.addObject("selectedStatus", docStatus);
		modelAndView.addObject("selectedDate", date);
		modelAndView.addObject("selectedDateControl", dateControl);
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
