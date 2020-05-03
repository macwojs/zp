package edu.agh.zp.services;

import edu.agh.zp.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class StorageService {

	@Value ( "${upload.path}" )
	private String path;

	public String uploadFile( MultipartFile file ) {

		if ( file.isEmpty( ) ) {

			throw new StorageException( "Failed to store empty file" );
		}

		try {
			var fileName = file.getOriginalFilename( );
			var is = file.getInputStream( );

			File file_path = new File( path );
			String absolutePath = file_path.getAbsolutePath( );

			Files.copy( is, Paths.get( absolutePath + '/' + fileName ), StandardCopyOption.REPLACE_EXISTING );

			return path + fileName;
		} catch ( IOException e ) {

			var msg = String.format( "Failed to store file %s", file.getName( ) );

			throw new StorageException( msg, e );
		}
	}
}
