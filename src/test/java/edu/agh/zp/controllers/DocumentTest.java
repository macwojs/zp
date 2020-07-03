package edu.agh.zp.controllers;
import static org.assertj.core.api.Assertions.assertThat;
import edu.agh.zp.objects.DocumentEntity;
import edu.agh.zp.repositories.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.*;
import java.time.LocalTime;
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
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
    public static long addDocumentStatusPresident(MockMvc mockMvc, DocumentRepository dR) throws Exception {
        File initialFile = new File("src/test/java/edu/agh/zp/resources/Looks_Like.pdf");
        InputStream targetStream = new DataInputStream(new FileInputStream(initialFile));
        MockMultipartFile file = new MockMultipartFile("file","test.pdf", "application/pdf", targetStream);
        String timeStr = LocalTime.now().toString();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/parlament/documentForm")
                .file(file)
                .characterEncoding("UTF-8")
                .param("docTypeID", "1")
                .param("docName", "Ustawa dla Prezydenta_"+timeStr)
                .param("docDescription", "Ustawa Test")
                .param("docStatusID", "7")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/ustawy/*"));
        Optional<DocumentEntity> docTemp = dR.findByDocNameAndDocDescription("Ustawa dla Prezydenta_"+timeStr, "Ustawa Test");
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
                .param("docTypeID", "1")
                .param("docName", "Ustawa Test"+timeStr)
                .param("docDescription", "Ustawa Test")
                .param("docStatusID", "2")
                .with(user("marszaleksenatu@zp.pl").roles("MARSZALEK_SENATU"))
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/ustawy/*"));
        Optional<DocumentEntity> docTemp = dR.findByDocNameAndDocDescription("Ustawa Test"+timeStr, "Ustawa Test");
        assertThat(docTemp.isPresent()).isEqualTo(true);
        return docTemp.orElseThrow().getDocID();
    }

    public static DocumentEntity addAnnotation(long docID, DocumentRepository dR, MockMvc mockMvc, String timeStr ) throws Exception {
        File initialFile = new File("src/test/java/edu/agh/zp/resources/Looks_Like.pdf");
        InputStream targetStream = new DataInputStream(new FileInputStream(initialFile));
        MockMultipartFile file = new MockMultipartFile("file","test.pdf", "application/pdf", targetStream);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/ustawy/annotation/"+docID)
                .file(file)
                .characterEncoding("UTF-8")
                .param("docName", "Adnotacja_"+docID+"_"+timeStr)
                .param("docDescription", "Adnotacja")
                .param("docStatusID", "10")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/ustawy/*"));
        Optional<DocumentEntity> docTemp = dR.findByDocNameAndDocDescription("Adnotacja_"+docID+"_"+timeStr, "Adnotacja");
        assertThat(docTemp.isPresent()).isEqualTo(true);
        return docTemp.orElseThrow();
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

    @Test
    void changeDocumentDescriptionTest() throws Exception {
        long documentCountBefore = dR.count();
        String timeStr = LocalTime.now().toString();
        Optional<DocumentEntity> doc1 = dR.findByDocNameAndDocDescription("Ustawa Test"+timeStr, "Ustawa Test");
        assertThat(doc1.isEmpty()).isEqualTo(true);
        long docID = addDocumentSejm(mockMvc, dR);
        long documentCountAfter = dR.count();
        assertThat(documentCountAfter).isEqualTo(documentCountBefore+1);
        assertThat(dR.findByDocID(docID).orElseThrow().getDocDescription()).isEqualTo("Ustawa Test");
        mockMvc.perform(post("/ustawy/description/"+docID)
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("desc", "Nowy Opis Ustawy")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/ustawy/*"));
        assertThat(dR.findByDocID(docID).orElseThrow().getDocDescription()).isEqualTo("Nowy Opis Ustawy");
        dR.deleteById(docID);
    }

    @Test
    void changeDocumentStatusFromSejmToSenatTest() throws Exception {
        long documentCountBefore = dR.count();
        String timeStr = LocalTime.now().toString();
        Optional<DocumentEntity> doc1 = dR.findByDocNameAndDocDescription("Ustawa Test"+timeStr, "Ustawa Test");
        assertThat(doc1.isEmpty()).isEqualTo(true);
        long docID = addDocumentSejm(mockMvc, dR);
        long documentCountAfter = dR.count();
        assertThat(documentCountAfter).isEqualTo(documentCountBefore+1);
        assertThat(dR.findByDocID(docID).orElseThrow().getDocStatusID().getDocStatusName()).isEqualTo("Głosowanie w Sejmie");

        mockMvc.perform(get("/ustawy/status/"+docID)
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU")))
                .andExpect(content().string(containsString("<option value=\"4\">Odrzucona</option>")))
                .andExpect(content().string(containsString("<option value=\"2\">Głosowanie w Senacie</option>")))
                .andExpect(content().string(not(containsString("<option value=\"8\">Do ponownego rozpatrzenia w Sejmie: Senat</option>"))))
                .andExpect(content().string(not(containsString("<option value=\"9\">Do ponownego rozpatrzenia w Sejmie: Prezydent</option>"))))
                .andExpect(content().string(not(containsString("<option value=\"7\">Do zatwierdzenia przez Prezydenta</option>"))))
                .andExpect(content().string(not(containsString("<option value=\"1\">Głosowanie w Sejmie</option>"))));

        mockMvc.perform(post("/ustawy/status/"+docID)
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("type", "2")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/ustawy/*"));
        assertThat(dR.findByDocID(docID).orElseThrow().getDocStatusID().getDocStatusName()).isEqualTo("Głosowanie w Senacie");
        dR.deleteById(docID);
    }

    @Test
    void changeDocumentStatusToRejectedTest() throws Exception {
        long documentCountBefore = dR.count();
        String timeStr = LocalTime.now().toString();
        Optional<DocumentEntity> doc1 = dR.findByDocNameAndDocDescription("Ustawa Test"+timeStr, "Ustawa Test");
        assertThat(doc1.isEmpty()).isEqualTo(true);
        long docID = addDocumentSejm(mockMvc, dR);
        long documentCountAfter = dR.count();
        assertThat(documentCountAfter).isEqualTo(documentCountBefore+1);
        assertThat(dR.findByDocID(docID).orElseThrow().getDocStatusID().getDocStatusName()).isEqualTo("Głosowanie w Sejmie");

        mockMvc.perform(get("/ustawy/status/"+docID)
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU")))
                .andExpect(content().string(containsString("<option value=\"4\">Odrzucona</option>")))
                .andExpect(content().string(containsString("<option value=\"2\">Głosowanie w Senacie</option>")))
                .andExpect(content().string(not(containsString("<option value=\"8\">Do ponownego rozpatrzenia w Sejmie: Senat</option>"))))
                .andExpect(content().string(not(containsString("<option value=\"9\">Do ponownego rozpatrzenia w Sejmie: Prezydent</option>"))))
                .andExpect(content().string(not(containsString("<option value=\"7\">Do zatwierdzenia przez Prezydenta</option>"))))
                .andExpect(content().string(not(containsString("<option value=\"1\">Głosowanie w Sejmie</option>"))));

        mockMvc.perform(post("/ustawy/status/"+docID)
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("type", "4")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/ustawy/*"));
        assertThat(dR.findByDocID(docID).orElseThrow().getDocStatusID().getDocStatusName()).isEqualTo("Odrzucona");
        dR.deleteById(docID);
    }

    @Test
    void changeDocumentStatusFromRejectedTest() throws Exception {
        long documentCountBefore = dR.count();
        String timeStr = LocalTime.now().toString();
        Optional<DocumentEntity> doc1 = dR.findByDocNameAndDocDescription("Ustawa Test"+timeStr, "Ustawa Test");
        assertThat(doc1.isEmpty()).isEqualTo(true);
        long docID = addDocumentSejm(mockMvc, dR);
        long documentCountAfter = dR.count();
        assertThat(documentCountAfter).isEqualTo(documentCountBefore+1);
        assertThat(dR.findByDocID(docID).orElseThrow().getDocStatusID().getDocStatusName()).isEqualTo("Głosowanie w Sejmie");

        mockMvc.perform(get("/ustawy/status/"+docID)
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU")))
                .andExpect(content().string(containsString("<option value=\"4\">Odrzucona</option>")))
                .andExpect(content().string(containsString("<option value=\"2\">Głosowanie w Senacie</option>")))
                .andExpect(content().string(not(containsString("<option value=\"8\">Do ponownego rozpatrzenia w Sejmie: Senat</option>"))))
                .andExpect(content().string(not(containsString("<option value=\"9\">Do ponownego rozpatrzenia w Sejmie: Prezydent</option>"))))
                .andExpect(content().string(not(containsString("<option value=\"7\">Do zatwierdzenia przez Prezydenta</option>"))))
                .andExpect(content().string(not(containsString("<option value=\"1\">Głosowanie w Sejmie</option>"))));

        mockMvc.perform(post("/ustawy/status/"+docID)
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("type", "4")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/ustawy/*"));
        assertThat(dR.findByDocID(docID).orElseThrow().getDocStatusID().getDocStatusName()).isEqualTo("Odrzucona");

        mockMvc.perform(get("/ustawy/status/"+docID)
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nie można zmienić statusu tej ustawy")));
        dR.deleteById(docID);
    }

    @Test
    void changeDocumentStatusFromSenatToSejmTest() throws Exception {
        long documentCountBefore = dR.count();
        String timeStr = LocalTime.now().toString();
        Optional<DocumentEntity> doc1 = dR.findByDocNameAndDocDescription("Ustawa Test"+timeStr, "Ustawa Test");
        assertThat(doc1.isEmpty()).isEqualTo(true);
        long docID = addDocumentSenat(mockMvc, dR);
        long documentCountAfter = dR.count();
        assertThat(documentCountAfter).isEqualTo(documentCountBefore+1);
        assertThat(dR.findByDocID(docID).orElseThrow().getDocStatusID().getDocStatusName()).isEqualTo("Głosowanie w Senacie");

        mockMvc.perform(get("/ustawy/status/"+docID)
                .with(user("marszaleksenatu@zp.pl").roles("MARSZALEK_SENATU")))
                .andExpect(content().string(containsString("<option value=\"4\">Odrzucona</option>")))
                .andExpect(content().string(not(containsString("<option value=\"2\">Głosowanie w Senacie</option>"))))
                .andExpect(content().string(containsString("<option value=\"8\">Do ponownego rozpatrzenia w Sejmie: Senat</option>")))
                .andExpect(content().string(not(containsString("<option value=\"9\">Do ponownego rozpatrzenia w Sejmie: Prezydent</option>"))))
                .andExpect(content().string(containsString("<option value=\"7\">Do zatwierdzenia przez Prezydenta</option>")))
                .andExpect(content().string(not(containsString("<option value=\"1\">Głosowanie w Sejmie</option>"))));

        mockMvc.perform(post("/ustawy/status/"+docID)
                .with(user("marszaleksenatu@zp.pl").roles("MARSZALEK_SENATU"))
                .param("type", "1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/ustawy/*"));
        assertThat(dR.findByDocID(docID).orElseThrow().getDocStatusID().getDocStatusName()).isEqualTo("Głosowanie w Sejmie");
        dR.deleteById(docID);
    }

//    @Test
//    void addAnnotationToDocumentTest() throws Exception {
//        String timeStr = LocalTime.now().toString();
//        Optional<DocumentEntity> doc1 = dR.findByDocNameAndDocDescription("Ustawa Test"+timeStr, "Ustawa Test");
//        assertThat(doc1.isEmpty()).isEqualTo(true);
//        long docID = addDocumentSejm(mockMvc, dR);
//        long documentCountAfter = dR.count();
//        DocumentEntity annotation= addAnnotation(docID, dR, mockMvc, timeStr);
//        assertThat(dR.count()).isEqualTo(documentCountAfter+1);
//        dR.deleteById(annotation.getDocID());
//        dR.deleteById(docID);
//    }


    @Test
    void presidentSignTheAct() throws Exception {
        long documentCountBefore = dR.count();
        String timeStr = LocalTime.now().toString();
        Optional<DocumentEntity> doc1 = dR.findByDocNameAndDocDescription("Ustawa dla Prezydenta_"+timeStr, "Ustawa Test");
        assertThat(doc1.isEmpty()).isEqualTo(true);
        long docID = addDocumentStatusPresident(mockMvc, dR);
        long documentCountAfter = dR.count();
        assertThat(documentCountAfter).isEqualTo(documentCountBefore+1);
        assertThat(dR.findByDocID(docID).orElseThrow().getDocStatusID().getDocStatusName()).isEqualTo("Do zatwierdzenia przez Prezydenta");

        mockMvc.perform(get("/prezydent/podpisz/1/"+docID)
                .with(user("prezydent@zp.pl").roles("PREZYDENT")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/prezydent/podpisz"));
        assertThat(dR.findByDocID(docID).orElseThrow().getDocStatusID().getDocStatusName()).isEqualTo("Przyjęta");
        dR.deleteById(docID);
    }

    @Test
    void presidentRejectAct() throws Exception {
        long documentCountBefore = dR.count();
        String timeStr = LocalTime.now().toString();
        Optional<DocumentEntity> doc1 = dR.findByDocNameAndDocDescription("Ustawa dla Prezydenta_"+timeStr, "Ustawa Test");
        assertThat(doc1.isEmpty()).isEqualTo(true);
        long docID = addDocumentStatusPresident(mockMvc, dR);
        long documentCountAfter = dR.count();
        assertThat(documentCountAfter).isEqualTo(documentCountBefore+1);
        assertThat(dR.findByDocID(docID).orElseThrow().getDocStatusID().getDocStatusName()).isEqualTo("Do zatwierdzenia przez Prezydenta");

        mockMvc.perform(get("/prezydent/podpisz/0/"+docID)
                .with(user("prezydent@zp.pl").roles("PREZYDENT")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/prezydent/podpisz"));
        assertThat(dR.findByDocID(docID).orElseThrow().getDocStatusID().getDocStatusName()).isEqualTo("Do ponownego rozpatrzenia w Sejmie: Prezydent");
        dR.deleteById(docID);
    }

}
