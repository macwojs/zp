package edu.agh.zp.controller;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.DocumentEntity;
import edu.agh.zp.objects.DocumentStatusEntity;
import edu.agh.zp.objects.DocumentTypeEntity;
import edu.agh.zp.repositories.DocumentRepository;
import edu.agh.zp.repositories.DocumentStatusRepository;
import edu.agh.zp.repositories.DocumentTypeRepository;
import edu.agh.zp.services.CitizenService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.swing.text.Document;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping ( value = { "/parlament" } )
public class ParlamentController {

	private static String UPLOADED_FOLDER = "/home/maciek/Temp/";
	//private static String UPLOADED_FOLDER = "/static/uploaded/";

	private DocumentTypeRepository documentTypeRepository;
	private DocumentStatusRepository documentStatusRepository;
	private DocumentRepository documentRepository;

	public ParlamentController(
			DocumentTypeRepository documentTypeRepository,
			DocumentStatusRepository documentStatusRepository,
			DocumentRepository documentRepository) {
		this.documentTypeRepository = documentTypeRepository;
		this.documentStatusRepository = documentStatusRepository;
		this.documentRepository = documentRepository;
	}

	@GetMapping ( value = { "" } )
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "parlament" );
		return modelAndView;
	}


	@GetMapping ( value = { "/ustawy" } )
	public ModelAndView ustawy() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName( "ustawy" );
		return modelAndView;
	}

	@GetMapping ( value = { "/documentForm" } )
	public ModelAndView documentForm( ModelAndView model ) {
		List< DocumentTypeEntity > types = documentTypeRepository.findAll( );
		model.addObject( "types", types );

		List< DocumentStatusEntity > statuses = documentStatusRepository.findAll( );
		model.addObject( "statuses", statuses );

		model.addObject( "document", new DocumentEntity( ) );

		model.setViewName( "documentForm" );
		return model;
	}

	@PostMapping ( value = { "/documentForm" })
	public ModelAndView submitRegister(
			@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes,
			@Valid @ModelAttribute("document") DocumentEntity document,
			BindingResult res){
		if( res.hasErrors()){
			return new ModelAndView("documentForm");
		}

		if(file.isEmpty()){
			return new ModelAndView("documentForm");
		}

		documentRepository.save( document );

		try {

			// Get the file and save it somewhere
			byte[] bytes = file.getBytes();
			Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
			Files.write(path, bytes);

			redirectAttributes.addFlashAttribute("message",
					"You successfully uploaded '" + file.getOriginalFilename() + "'");

		} catch ( IOException e) {
			e.printStackTrace();
		}

		RedirectView redirect = new RedirectView();
		redirect.setUrl("/parlament");
		return new ModelAndView(redirect);
	}
}
