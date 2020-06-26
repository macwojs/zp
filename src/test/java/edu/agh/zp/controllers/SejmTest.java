package edu.agh.zp.controllers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import edu.agh.zp.objects.DocumentEntity;
import edu.agh.zp.objects.VotingEntity;
import edu.agh.zp.repositories.DocumentRepository;
import edu.agh.zp.repositories.VotingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Time;
import java.time.LocalDate;
import java.util.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SejmTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VotingRepository vR;

    @Autowired
    private DocumentRepository dR;

    @Test
    void addVotingInSejm() throws Exception {
        File initialFile = new File("src/test/java/edu/agh/zp/resources/Looks_Like.pdf");
        InputStream targetStream = new DataInputStream(new FileInputStream(initialFile));
        MockMultipartFile file = new MockMultipartFile("file","test.pdf", "application/pdf", targetStream);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/parlament/documentForm")
                .file(file)
                .characterEncoding("UTF-8")
                .param("docTypeID", "1")
                .param("docName", "Ustawa Test")
                .param("docDescription", "Ustawa Test")
                .param("docStatusID", "1")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/ustawy/*"));
        Optional<DocumentEntity> docTemp = dR.findByDocNameAndDocDescription("Ustawa Test", "Ustawa Test");
        Long docID = docTemp.orElseThrow().getDocID();


        LocalDate votingDate = LocalDate.now().plusDays(5);
        Time openTime = Time.valueOf("12:00:00");
        Time closeTime = Time.valueOf("12:05:00");
        long votingCountBefore = vR.count();
        mockMvc.perform(post("/parlament/sejm/voteAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("documentID", docID.toString())
                .param("votingDate", votingDate.toString())
                .param("open", "12:00:00")
                .param("close", "12:05:00")
                .with(csrf()))
                .andExpect(redirectedUrl("/parlament/sejm"));
        List<VotingEntity> list = vR.findAll();
        assertThat(list.size()).isEqualTo(votingCountBefore+1);
        Optional<VotingEntity> votingTemp = vR.findById(list.get((int) votingCountBefore).getVotingID());
        VotingEntity voting = votingTemp.orElseThrow();
        assertThat( voting.getOpenVoting() ).isEqualTo(openTime);
        assertThat( voting.getCloseVoting() ).isEqualTo(closeTime);
        assertThat( voting.getDocumentID().getDocID() ).isEqualTo(docID);
        vR.deleteById(voting.getVotingID());
        dR.deleteById(docID);
    }

    @Test
    void addVotingInSejmYesterday() throws Exception {
        File initialFile = new File("src/test/java/edu/agh/zp/resources/Looks_Like.pdf");
        InputStream targetStream = new DataInputStream(new FileInputStream(initialFile));
        MockMultipartFile file = new MockMultipartFile("file","test.pdf", "application/pdf", targetStream);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/parlament/documentForm")
                .file(file)
                .characterEncoding("UTF-8")
                .param("docTypeID", "1")
                .param("docName", "Ustawa Test")
                .param("docDescription", "Ustawa Test")
                .param("docStatusID", "1")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/ustawy/*"));
        Optional<DocumentEntity> docTemp = dR.findByDocNameAndDocDescription("Ustawa Test", "Ustawa Test");
        
        Long docID = docTemp.orElseThrow().getDocID();
        LocalDate votingDate = LocalDate.now().minusDays(1);
        long votingCountBefore = vR.count();
        mockMvc.perform(post("/parlament/sejm/voteAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("documentID", docID.toString())
                .param("votingDate", votingDate.toString())
                .param("open", "12:00:00")
                .param("close", "12:05:00")
                .with(csrf()))
                .andExpect(content().string(containsString("Nie można dodać głosowania w przeszłości (Czas).")))
                .andExpect(content().string(containsString("Nie można dodać głosowania w przeszłości (Data).")));
        List<VotingEntity> list = vR.findAll();
        assertThat(list.size()).isEqualTo((int) votingCountBefore);
        dR.deleteById(docID);
    }




}
