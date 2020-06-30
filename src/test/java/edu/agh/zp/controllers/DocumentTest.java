package edu.agh.zp.controllers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

import edu.agh.zp.ZpApplication;
import edu.agh.zp.classes.TimeProvider;
import edu.agh.zp.objects.DocumentEntity;
import edu.agh.zp.repositories.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DocumentTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentRepository dR;

    public static long addDocumentSejm(MockMvc mockMvc, DocumentRepository dR) throws Exception {
        File initialFile = new File("src/test/java/edu/agh/zp/resources/Looks_Like.pdf");
        InputStream targetStream = new DataInputStream(new FileInputStream(initialFile));
        MockMultipartFile file = new MockMultipartFile("file","test.pdf", "application/pdf", targetStream);
        String timeStr = LocalTime.now().toString();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/parlament/documentForm")
                .file(file)
                .characterEncoding("UTF-8")
                .param("docTypeID", "1")
                .param("docName", "Ustawa Test"+timeStr)
                .param("docDescription", "Ustawa Test")
                .param("docStatusID", "1")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/ustawy/*"));
        Optional<DocumentEntity> docTemp = dR.findByDocNameAndDocDescription("Ustawa Test"+timeStr, "Ustawa Test");
        assertThat(docTemp.isPresent()).isEqualTo(true);
        return docTemp.orElseThrow().getDocID();
    }

    public static long addDocumentSenat(MockMvc mockMvc, DocumentRepository dR) throws Exception {
        File initialFile = new File("src/test/java/edu/agh/zp/resources/Looks_Like.pdf");
        InputStream targetStream = new DataInputStream(new FileInputStream(initialFile));
        MockMultipartFile file = new MockMultipartFile("file","test.pdf", "application/pdf", targetStream);
        String timeStr = LocalTime.now().toString();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/parlament/documentForm")
                .file(file)
                .characterEncoding("UTF-8")
                .param("docTypeID", "2")
                .param("docName", "Ustawa Test"+timeStr)
                .param("docDescription", "Ustawa Test")
                .param("docStatusID", "1")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/ustawy/*"));
        Optional<DocumentEntity> docTemp = dR.findByDocNameAndDocDescription("Ustawa Test"+timeStr, "Ustawa Test");
        assertThat(docTemp.isPresent()).isEqualTo(true);
        return docTemp.orElseThrow().getDocID();
    }

    @Test
    void addDocumentTest() throws Exception {
        long documentCountBefore = dR.count();
        String timeStr = LocalTime.now().toString();
        Optional<DocumentEntity> doc1 = dR.findByDocNameAndDocDescription("Ustawa Test"+timeStr, "Ustawa Test");
        assertThat(doc1.isEmpty()).isEqualTo(true);
        long docID = addDocumentSejm(mockMvc, dR);
        long documentCountAfter = dR.count();
        assertThat(documentCountAfter).isEqualTo(documentCountBefore+1);
        dR.deleteById(docID);
    }


}
