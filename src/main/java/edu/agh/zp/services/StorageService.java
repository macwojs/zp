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
import java.sql.Timestamp;
import java.util.Objects;


@Service
public class StorageService {

	@Value ( "${upload.path}" )
	private String path;

	public String uploadFile( MultipartFile file ) {

		if ( file.isEmpty( ) ) {

			throw new StorageException( "Failed to store empty file" );
		}

		try {
			Timestamp timeStamp = new Timestamp( System.currentTimeMillis( ) );
			String fileExt = com.google.common.io.Files.getFileExtension( Objects.requireNonNull( file.getOriginalFilename( ) ) );
			String fileNameWOExt = com.google.common.io.Files.getNameWithoutExtension( Objects.requireNonNull( file.getOriginalFilename( ) ) );
			var fileName = fileNameWOExt + "_" + timeStamp.toString( ) + "." + fileExt;
			var is = file.getInputStream( );

			File filePath = new File( path );
			String absolutePath = filePath.getAbsolutePath( );

			Files.copy( is, Paths.get( absolutePath + '/' + fileName ), StandardCopyOption.REPLACE_EXISTING );

			return path + fileName;
		} catch ( IOException e ) {

			var msg = String.format( "Failed to store file %s", file.getName( ) );

			throw new StorageException( msg, e );
		}
	}
}
