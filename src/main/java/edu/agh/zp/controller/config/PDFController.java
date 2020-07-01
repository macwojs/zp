package edu.agh.zp.controller.config;



import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Controller;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;

@Controller
@RequestMapping(value={"/src/main/resources/static/uploaded"})
public class PDFController {

    @Value("${upload.path}")
    private String path;

    @RequestMapping("/{fileName:.+}")
    public void downloadPDFResource( HttpServletRequest request, HttpServletResponse response,
                                     @PathVariable("fileName") String fileName){
        try{
            File file = new File(path + fileName);
            if (file.exists()) {

                //get the mimetype
                String mimeType = URLConnection.guessContentTypeFromName(file.getName());
                if (mimeType == null) {
                    //unknown mimetype so set the mimetype to application/octet-stream
                    mimeType = "application/octet-stream";
                }

                response.setContentType(mimeType);
                response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"" );
                response.setContentLength((int) file.length());
                InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                FileCopyUtils.copy(inputStream, response.getOutputStream());
            }
        } catch ( IOException e ){
            throw new RuntimeException( "IOError during get file output stream");
        }

    }
}