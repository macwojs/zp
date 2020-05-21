package edu.agh.zp.controller;



import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping(value={"/src/main/resources/static/uploaded"})
public class PDFController {

    @Value("${upload.path}")
    private String path;

    @GetMapping(value = {"/{doc}"} , produces = "application/pdf")
    public void PDFDisplay(@PathVariable String doc, HttpServletResponse response) throws IOException {
        try {
            DefaultResourceLoader loader = new DefaultResourceLoader();
            InputStream is = loader.getResource("classpath:static/uploaded/"+doc+".pdf").getInputStream();
            IOUtils.copy(is, response.getOutputStream());
            response.setHeader("Content-Disposition", "attachment; filename=doc.pdf");
            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        }
    }
}