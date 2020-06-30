package edu.agh.zp.controllers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SenatTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VotingRepository vR;

    @Autowired
    private DocumentRepository dR;

    @Test
    void addVotingInSenat() throws Exception {
        File initialFile = new File("src/test/java/edu/agh/zp/resources/Looks_Like.pdf");
        InputStream targetStream = new DataInputStream(new FileInputStream(initialFile));
        MockMultipartFile file = new MockMultipartFile("file","test.pdf", "application/pdf", targetStream);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/parlament/documentForm")
                .file(file)
                .characterEncoding("UTF-8")
                .param("docTypeID", "1")
                .param("docName", "Ustawa Test")
                .param("docDescription", "Ustawa Test")
                .param("docStatusID", "2")
                .with(user("marszaleksenatu@zp.pl").roles("MARSZALEK_SENATU"))
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/ustawy/*"));
        Optional<DocumentEntity> docTemp = dR.findByDocNameAndDocDescription("Ustawa Test", "Ustawa Test");
        Long docID = docTemp.orElseThrow().getDocID();


        LocalDate votingDate = LocalDate.now().plusDays(5);
        Time openTime = Time.valueOf("12:00:00");
        Time closeTime = Time.valueOf("12:05:00");
        long votingCountBefore = vR.count();
        mockMvc.perform(post("/parlament/senat/voteAdd")
                .with(user("marszaleksenatu@zp.pl").roles("MARSZALEK_SENATU"))
                .param("documentID", docID.toString())
                .param("votingDate", votingDate.toString())
                .param("open", "12:00:00")
                .param("close", "12:05:00")
                .with(csrf()))
                .andExpect(redirectedUrl("/parlament/senat"));
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
    void addVotingInsenatYesterday() throws Exception {
        File initialFile = new File("src/test/java/edu/agh/zp/resources/Looks_Like.pdf");
        InputStream targetStream = new DataInputStream(new FileInputStream(initialFile));
        MockMultipartFile file = new MockMultipartFile("file","test.pdf", "application/pdf", targetStream);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/parlament/documentForm")
                .file(file)
                .characterEncoding("UTF-8")
                .param("docTypeID", "1")
                .param("docName", "Ustawa Test")
                .param("docDescription", "Ustawa Test")
                .param("docStatusID", "2")
                .with(user("marszaleksenatu@zp.pl").roles("MARSZALEK_SENATU"))
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/ustawy/*"));
        Optional<DocumentEntity> docTemp = dR.findByDocNameAndDocDescription("Ustawa Test", "Ustawa Test");

        Long docID = docTemp.orElseThrow().getDocID();
        LocalDate votingDate = LocalDate.now().minusDays(1);
        long votingCountBefore = vR.count();
        mockMvc.perform(post("/parlament/senat/voteAdd")
                .with(user("marszaleksenatu@zp.pl").roles("MARSZALEK_SENATU"))
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

    @Test
    void displayTodaysVotings() throws Exception {
        File initialFile = new File("src/test/java/edu/agh/zp/resources/Looks_Like.pdf");
        InputStream targetStream = new DataInputStream(new FileInputStream(initialFile));
        MockMultipartFile file = new MockMultipartFile("file","test.pdf", "application/pdf", targetStream);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/parlament/documentForm")
                .file(file)
                .characterEncoding("UTF-8")
                .param("docTypeID", "1")
                .param("docName", "Ustawa Test")
                .param("docDescription", "Ustawa Test")
                .param("docStatusID", "2")
                .with(user("marszaleksenatu@zp.pl").roles("MARSZALEK_SENATU"))
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/ustawy/*"));
        Optional<DocumentEntity> docTemp = dR.findByDocNameAndDocDescription("Ustawa Test", "Ustawa Test");
        Long docID = docTemp.orElseThrow().getDocID();

        long votingCountBefore = vR.count();

        LocalDate votingDate = LocalDate.now();
        LocalTime openTime1 = LocalTime.now();
        LocalTime closeTime1 = openTime1.plusMinutes(1);
        mockMvc.perform(post("/parlament/senat/voteAdd")
                .with(user("marszaleksenatu@zp.pl").roles("MARSZALEK_SENATU"))
                .param("documentID", docID.toString())
                .param("votingDate", votingDate.toString())
                .param("open", openTime1.toString())
                .param("close", closeTime1.toString())
                .with(csrf()))
                .andExpect(redirectedUrl("/parlament/senat"));
        LocalTime openTime2 = LocalTime.now().plusMinutes(1);
        LocalTime closeTime2 = openTime2.plusMinutes(2);
        mockMvc.perform(post("/parlament/senat/voteAdd")
                .with(user("marszaleksenatu@zp.pl").roles("MARSZALEK_SENATU"))
                .param("documentID", docID.toString())
                .param("votingDate", votingDate.toString())
                .param("open", openTime2.toString())
                .param("close", closeTime2.toString())
                .with(csrf()))
                .andExpect(redirectedUrl("/parlament/senat"));
        LocalTime openTime3 = LocalTime.now().plusMinutes(2);
        LocalTime closeTime3 = openTime3.plusMinutes(3);
        mockMvc.perform(post("/parlament/senat/voteAdd")
                .with(user("marszaleksenatu@zp.pl").roles("MARSZALEK_SENATU"))
                .param("documentID", docID.toString())
                .param("votingDate", votingDate.toString())
                .param("open", openTime3.toString())
                .param("close", closeTime3.toString())
                .with(csrf()))
                .andExpect(redirectedUrl("/parlament/senat"));
        List<VotingEntity> list = vR.findAll();
        assertThat(list.size()).isEqualTo(votingCountBefore+3);

        mockMvc.perform(get("/parlament/senat/votingSchedule"))
                .andExpect(content().string(containsString(openTime1.toString().substring(0,8)+"</span> - <span>"+closeTime1.toString().substring(0,8))))
                .andExpect(content().string(containsString(openTime2.toString().substring(0,8)+"</span> - <span>"+closeTime2.toString().substring(0,8))))
                .andExpect(content().string(containsString(openTime3.toString().substring(0,8)+"</span> - <span>"+closeTime3.toString().substring(0,8))));

        vR.deleteById(list.get((int) votingCountBefore).getVotingID());
        vR.deleteById(list.get((int) votingCountBefore+1).getVotingID());
        vR.deleteById(list.get((int) votingCountBefore+2).getVotingID());
        dR.deleteById(docID);
    }

    @Test
    void displayTomorrowsVotings() throws Exception {
        File initialFile = new File("src/test/java/edu/agh/zp/resources/Looks_Like.pdf");
        InputStream targetStream = new DataInputStream(new FileInputStream(initialFile));
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", targetStream);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/parlament/documentForm")
                .file(file)
                .characterEncoding("UTF-8")
                .param("docTypeID", "2")
                .param("docName", "Ustawa Test")
                .param("docDescription", "Ustawa Test")
                .param("docStatusID", "2")
                .with(user("marszaleksenatu@zp.pl").roles("MARSZALEK_SENATU"))
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/ustawy/*"));
        Optional<DocumentEntity> docTemp = dR.findByDocNameAndDocDescription("Ustawa Test", "Ustawa Test");
        Long docID = docTemp.orElseThrow().getDocID();

        long votingCountBefore = vR.count();

        LocalDate votingDate1 = LocalDate.now();
        LocalDate votingDate2 = LocalDate.now().plusDays(1);
        LocalTime openTime1 = LocalTime.now();
        LocalTime closeTime1 = openTime1.plusMinutes(1);
        mockMvc.perform(post("/parlament/senat/voteAdd")
                .with(user("marszaleksenatu@zp.pl").roles("MARSZALEK_SENATU"))
                .param("documentID", docID.toString())
                .param("votingDate", votingDate1.toString())
                .param("open", openTime1.toString())
                .param("close", closeTime1.toString())
                .with(csrf()))
                .andExpect(redirectedUrl("/parlament/senat"));
        LocalTime openTime2 = LocalTime.now().plusMinutes(1);
        LocalTime closeTime2 = openTime2.plusMinutes(2);
        mockMvc.perform(post("/parlament/senat/voteAdd")
                .with(user("marszaleksenatu@zp.pl").roles("MARSZALEK_SENATU"))
                .param("documentID", docID.toString())
                .param("votingDate", votingDate2.toString())
                .param("open", openTime2.toString())
                .param("close", closeTime2.toString())
                .with(csrf()))
                .andExpect(redirectedUrl("/parlament/senat"));
        LocalTime openTime3 = LocalTime.now().plusMinutes(2);
        LocalTime closeTime3 = openTime3.plusMinutes(3);
        mockMvc.perform(post("/parlament/senat/voteAdd")
                .with(user("marszaleksenatu@zp.pl").roles("MARSZALEK_SENATU"))
                .param("documentID", docID.toString())
                .param("votingDate", votingDate2.toString())
                .param("open", openTime3.toString())
                .param("close", closeTime3.toString())
                .with(csrf()))
                .andExpect(redirectedUrl("/parlament/senat"));
        List<VotingEntity> list = vR.findAll();
        assertThat(list.size()).isEqualTo(votingCountBefore + 3);

        String pattern = "dd MMMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("pl", "PL"));

        mockMvc.perform(get("/parlament/senat/votingSchedule?dateForm=" + votingDate2.toString()))
                .andExpect(content().string(containsString(simpleDateFormat.format(java.sql.Date.valueOf(votingDate2)))))
                .andExpect(content().string(not(containsString(simpleDateFormat.format(java.sql.Date.valueOf(votingDate1))))))
                .andExpect(content().string(not(containsString(openTime1.toString().substring(0, 8) + "</span> - <span>" + closeTime1.toString().substring(0, 8)))))
                .andExpect(content().string(containsString(openTime2.toString().substring(0, 8) + "</span> - <span>" + closeTime2.toString().substring(0, 8))))
                .andExpect(content().string(containsString(openTime3.toString().substring(0, 8) + "</span> - <span>" + closeTime3.toString().substring(0, 8))));

        vR.deleteById(list.get((int) votingCountBefore).getVotingID());
        vR.deleteById(list.get((int) votingCountBefore + 1).getVotingID());
        vR.deleteById(list.get((int) votingCountBefore + 2).getVotingID());
        dR.deleteById(docID);
    }
}
