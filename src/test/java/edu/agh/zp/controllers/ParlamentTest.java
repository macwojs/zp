package edu.agh.zp.controllers;

import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.*;
import edu.agh.zp.services.CitizenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
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

import org.springframework.format.annotation.DateTimeFormat;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@AutoConfigureMockMvc
public class ParlamentTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CitizenService cS;

    @Autowired
    private VotingRepository vR;

    @Autowired
    private VoteRepository voteR;

    @Autowired
    private DocumentRepository dR;

    @Test
    void addVoteAsPoselInSejmVoting() throws Exception {
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
        Long docID = docTemp.orElseThrow().getDocID();

        LocalDate votingDate = LocalDate.now();
        LocalTime openTime = LocalTime.now();
        LocalTime closeTime = openTime.plusMinutes(5);
        long votingCountBefore = vR.count();
        mockMvc.perform(post("/parlament/sejm/voteAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("documentID", docID.toString())
                .param("votingDate", votingDate.toString())
                .param("open", openTime.toString())
                .param("close", closeTime.toString())
                .with(csrf()))
                .andExpect(redirectedUrl("/parlament/sejm"));
        List<VotingEntity> list = vR.findAll();

        assertThat(list.size()).isEqualTo(votingCountBefore+1);
        Collections.sort(list, new Comparator<VotingEntity>() {

            public int compare(VotingEntity o1, VotingEntity o2) {
                long b = o2.getVotingID();
                long a = o1.getVotingID();
                return Long.compare(a, b);
            }
        });
        Optional<VotingEntity> votingTemp = vR.findById(list.get((int)votingCountBefore).getVotingID());
        VotingEntity voting = votingTemp.orElseThrow();
        CountDownLatch time = new CountDownLatch(1);
        time.await(1, TimeUnit.SECONDS);
        // 3 - za 5 wstrzymaj sie 4 - przeciw
        CitizenEntity posel = cS.findByEmail("posel2@zp.pl").orElseThrow();
        assertThat(voteR.findByCitizenIdVotingId(posel.getCitizenID(), voting.getVotingID()).isEmpty()).isEqualTo(true);
        mockMvc.perform(post("/parlament/vote/"+voting.getVotingID())
                        .param("votingRadio", "3")
                        .with(user("posel2@zp.pl").roles("POSEL"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/parlament/sejm"));

        Optional<VoteEntity> vote = voteR.findByCitizenID_CitizenIDAndVotingID_VotingID(posel.getCitizenID(), voting.getVotingID());
        assertThat(vote.isPresent()).isEqualTo(true);
        voteR.deleteById(vote.orElseThrow().getVoteID());
        vR.deleteById(voting.getVotingID());
        dR.deleteById(docID);
    }

    @Test
    void addVoteAsMarszalekSejmuInSejmVoting() throws Exception {
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
        Long docID = docTemp.orElseThrow().getDocID();

        LocalDate votingDate = LocalDate.now();
        LocalTime openTime = LocalTime.now();
        LocalTime closeTime = openTime.plusMinutes(5);
        long votingCountBefore = vR.count();
        mockMvc.perform(post("/parlament/sejm/voteAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("documentID", docID.toString())
                .param("votingDate", votingDate.toString())
                .param("open", openTime.toString())
                .param("close", closeTime.toString())
                .with(csrf()))
                .andExpect(redirectedUrl("/parlament/sejm"));
        List<VotingEntity> list = vR.findAll();

        assertThat(list.size()).isEqualTo(votingCountBefore+1);
        Collections.sort(list, new Comparator<VotingEntity>() {

            public int compare(VotingEntity o1, VotingEntity o2) {
                long b = o2.getVotingID();
                long a = o1.getVotingID();
                return Long.compare(a, b);
            }
        });
        Optional<VotingEntity> votingTemp = vR.findById(list.get((int)votingCountBefore).getVotingID());
        VotingEntity voting = votingTemp.orElseThrow();
        CountDownLatch time = new CountDownLatch(1);
        time.await(1, TimeUnit.SECONDS);
        // 3 - za 5 wstrzymaj sie 4 - przeciw
        CitizenEntity posel = cS.findByEmail("marszaleksejmu@zp.pl").orElseThrow();
        assertThat(voteR.findByCitizenIdVotingId(posel.getCitizenID(), voting.getVotingID()).isEmpty()).isEqualTo(true);
        mockMvc.perform(post("/parlament/vote/"+voting.getVotingID())
                .param("votingRadio", "3")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU", "POSEL"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/parlament/sejm"));

        Optional<VoteEntity> vote = voteR.findByCitizenID_CitizenIDAndVotingID_VotingID(posel.getCitizenID(), voting.getVotingID());
        assertThat(vote.isPresent()).isEqualTo(true);
        voteR.deleteById(vote.orElseThrow().getVoteID());
        vR.deleteById(voting.getVotingID());
        dR.deleteById(docID);
    }

    @Test
    void addVoteAsGroupOfPoselInSejmVoting() throws Exception {
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
        Long docID = docTemp.orElseThrow().getDocID();

        LocalDate votingDate = LocalDate.now();
        LocalTime openTime = LocalTime.now();
        LocalTime closeTime = openTime.plusMinutes(5);
        long votingCountBefore = vR.count();
        mockMvc.perform(post("/parlament/sejm/voteAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("documentID", docID.toString())
                .param("votingDate", votingDate.toString())
                .param("open", openTime.toString())
                .param("close", closeTime.toString())
                .with(csrf()))
                .andExpect(redirectedUrl("/parlament/sejm"));
        List<VotingEntity> list = vR.findAll();

        assertThat(list.size()).isEqualTo(votingCountBefore+1);
        Collections.sort(list, new Comparator<VotingEntity>() {

            public int compare(VotingEntity o1, VotingEntity o2) {
                long b = o2.getVotingID();
                long a = o1.getVotingID();
                return Long.compare(a, b);
            }
        });
        Optional<VotingEntity> votingTemp = vR.findById(list.get((int)votingCountBefore).getVotingID());
        VotingEntity voting = votingTemp.orElseThrow();
        CountDownLatch time = new CountDownLatch(1);
        time.await(1, TimeUnit.SECONDS);

        CitizenEntity posel1 = cS.findByEmail("posel1@zp.pl").orElseThrow();
        assertThat(voteR.findByCitizenIdVotingId(posel1.getCitizenID(), voting.getVotingID()).isEmpty()).isEqualTo(true);
        mockMvc.perform(post("/parlament/vote/"+voting.getVotingID())
                .param("votingRadio", "3")
                .with(user("posel1@zp.pl").roles("POSEL"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/parlament/sejm"));

        time.await(1, TimeUnit.MILLISECONDS);

        CitizenEntity posel2 = cS.findByEmail("posel2@zp.pl").orElseThrow();
        assertThat(voteR.findByCitizenIdVotingId(posel2.getCitizenID(), voting.getVotingID()).isEmpty()).isEqualTo(true);
        mockMvc.perform(post("/parlament/vote/"+voting.getVotingID())
                .param("votingRadio", "5")
                .with(user("posel2@zp.pl").roles("POSEL"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/parlament/sejm"));

        time.await(1, TimeUnit.MILLISECONDS);

        CitizenEntity posel3 = cS.findByEmail("posel3@zp.pl").orElseThrow();
        assertThat(voteR.findByCitizenIdVotingId(posel3.getCitizenID(), voting.getVotingID()).isEmpty()).isEqualTo(true);
        mockMvc.perform(post("/parlament/vote/"+voting.getVotingID())
                .param("votingRadio", "4")
                .with(user("posel3@zp.pl").roles("POSEL"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/parlament/sejm"));

        time.await(1, TimeUnit.MILLISECONDS);

        CitizenEntity marszalek = cS.findByEmail("marszaleksejmu@zp.pl").orElseThrow();
        assertThat(voteR.findByCitizenIdVotingId(marszalek.getCitizenID(), voting.getVotingID()).isEmpty()).isEqualTo(true);
        mockMvc.perform(post("/parlament/vote/"+voting.getVotingID())
                .param("votingRadio", "3")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU", "POSEL"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/parlament/sejm"));

        List<VoteEntity> votes = voteR.findAllByVotingID(voting);
        assertThat(votes.size()).isEqualTo(4);
        voteR.deleteAll(votes);
        vR.deleteById(voting.getVotingID());
        dR.deleteById(docID);
    }


}
