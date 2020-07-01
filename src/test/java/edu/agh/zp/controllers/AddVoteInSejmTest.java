package edu.agh.zp.controllers;

import edu.agh.zp.classes.TimeProvider;
import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.*;
import edu.agh.zp.services.CitizenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static edu.agh.zp.controllers.DocumentTest.addDocumentSejm;
import static edu.agh.zp.controllers.SejmTest.addVotingInSejmCorrectly;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.hamcrest.Matchers.containsString;
import edu.agh.zp.objects.VotingEntity;
import edu.agh.zp.repositories.DocumentRepository;
import edu.agh.zp.repositories.VotingRepository;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@SpringBootTest
@AutoConfigureMockMvc
public class AddVoteInSejmTest {
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

    private static final LocalDateTime NEWNOW = LocalDateTime.of(2020, 7, 1, 10, 10,0);
    private static final LocalDateTime NOW = LocalDateTime.now();


    @Test
    void addVoteAsPoselInSejmVoting() throws Exception {
        TimeProvider.useFixedClockAt(NOW);

        Long docID = addDocumentSejm(mockMvc, dR);

        LocalDate votingDate = LocalDate.now();
        LocalTime openTime = LocalTime.now();
        LocalTime closeTime = openTime.plusMinutes(5);

        VotingEntity voting = addVotingInSejmCorrectly(mockMvc, vR, docID, votingDate, openTime.toString(), closeTime.toString());

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
                .andExpect(redirectedUrlPattern("/wydarzenie/*"));

        Optional<VoteEntity> vote = voteR.findByCitizenID_CitizenIDAndVotingID_VotingID(posel.getCitizenID(), voting.getVotingID());
        assertThat(vote.isPresent()).isEqualTo(true);
        voteR.deleteById(vote.orElseThrow().getVoteID());
        vR.deleteById(voting.getVotingID());
        dR.deleteById(docID);
    }

    @Test
    void addVoteAsMarszalekSejmuInSejmVoting() throws Exception {
        TimeProvider.useFixedClockAt(NOW);
        Long docID = addDocumentSejm(mockMvc, dR);

        LocalDate votingDate = LocalDate.now();
        LocalTime openTime = LocalTime.now();
        LocalTime closeTime = openTime.plusMinutes(5);
        VotingEntity voting = addVotingInSejmCorrectly(mockMvc, vR, docID, votingDate, openTime.toString(), closeTime.toString());

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
                .andExpect(redirectedUrlPattern("/wydarzenie/*"));

        Optional<VoteEntity> vote = voteR.findByCitizenID_CitizenIDAndVotingID_VotingID(posel.getCitizenID(), voting.getVotingID());
        assertThat(vote.isPresent()).isEqualTo(true);
        voteR.deleteById(vote.orElseThrow().getVoteID());
        vR.deleteById(voting.getVotingID());
        dR.deleteById(docID);
    }

    @Test
    void addVoteAsGroupOfPoselInSejmVoting() throws Exception {
        TimeProvider.useFixedClockAt(NOW);
        Long docID = addDocumentSejm(mockMvc, dR);

        LocalDate votingDate = LocalDate.now();
        LocalTime openTime = LocalTime.now();
        LocalTime closeTime = openTime.plusMinutes(5);

        VotingEntity voting = addVotingInSejmCorrectly(mockMvc, vR, docID, votingDate, openTime.toString(), closeTime.toString());

        CountDownLatch time = new CountDownLatch(1);
        time.await(1, TimeUnit.SECONDS);

        CitizenEntity posel1 = cS.findByEmail("posel1@zp.pl").orElseThrow();
        assertThat(voteR.findByCitizenIdVotingId(posel1.getCitizenID(), voting.getVotingID()).isEmpty()).isEqualTo(true);
        mockMvc.perform(post("/parlament/vote/"+voting.getVotingID())
                .param("votingRadio", "3")
                .with(user("posel1@zp.pl").roles("POSEL"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/wydarzenie/*"));

        time.await(1, TimeUnit.MILLISECONDS);

        CitizenEntity posel2 = cS.findByEmail("posel2@zp.pl").orElseThrow();
        assertThat(voteR.findByCitizenIdVotingId(posel2.getCitizenID(), voting.getVotingID()).isEmpty()).isEqualTo(true);
        mockMvc.perform(post("/parlament/vote/"+voting.getVotingID())
                .param("votingRadio", "5")
                .with(user("posel2@zp.pl").roles("POSEL"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/wydarzenie/*"));

        time.await(1, TimeUnit.MILLISECONDS);

        CitizenEntity posel3 = cS.findByEmail("posel3@zp.pl").orElseThrow();
        assertThat(voteR.findByCitizenIdVotingId(posel3.getCitizenID(), voting.getVotingID()).isEmpty()).isEqualTo(true);
        mockMvc.perform(post("/parlament/vote/"+voting.getVotingID())
                .param("votingRadio", "4")
                .with(user("posel3@zp.pl").roles("POSEL"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/wydarzenie/*"));

        time.await(1, TimeUnit.MILLISECONDS);

        CitizenEntity marszalek = cS.findByEmail("marszaleksejmu@zp.pl").orElseThrow();
        assertThat(voteR.findByCitizenIdVotingId(marszalek.getCitizenID(), voting.getVotingID()).isEmpty()).isEqualTo(true);
        mockMvc.perform(post("/parlament/vote/"+voting.getVotingID())
                .param("votingRadio", "3")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU", "POSEL"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/wydarzenie/*"));

        List<VoteEntity> votes = voteR.findAllByVotingID(voting);
        assertThat(votes.size()).isEqualTo(4);
        voteR.deleteAll(votes);
        vR.deleteById(voting.getVotingID());
        dR.deleteById(docID);
    }

    @Test
    void votingBeforeOpening() throws Exception {
        TimeProvider.useFixedClockAt(NOW);
        Long docID = addDocumentSejm(mockMvc, dR);

        LocalDate votingDate = LocalDate.now();
        LocalTime openTime = LocalTime.now().plusMinutes(5);
        LocalTime closeTime = openTime.plusMinutes(5);
        VotingEntity voting = addVotingInSejmCorrectly(mockMvc, vR, docID, votingDate, openTime.toString(), closeTime.toString());

        CountDownLatch time = new CountDownLatch(1);
        time.await(1, TimeUnit.SECONDS);
        // 3 - za 5 wstrzymaj sie 4 - przeciw
        CitizenEntity posel = cS.findByEmail("posel2@zp.pl").orElseThrow();
        assertThat(voteR.findByCitizenIdVotingId(posel.getCitizenID(), voting.getVotingID()).isEmpty()).isEqualTo(true);
        mockMvc.perform(post("/parlament/vote/"+voting.getVotingID())
                .param("votingRadio", "3")
                .with(user("posel2@zp.pl").roles("POSEL"))
                .with(csrf()))
                .andExpect(status().isForbidden());

        Optional<VoteEntity> vote = voteR.findByCitizenID_CitizenIDAndVotingID_VotingID(posel.getCitizenID(), voting.getVotingID());
        assertThat(vote.isEmpty()).isEqualTo(true);

        vR.deleteById(voting.getVotingID());
        dR.deleteById(docID);
    }

    @Test
    void votingAfterClosing() throws Exception {
        TimeProvider.useFixedClockAt(NEWNOW);

        Long docID = addDocumentSejm(mockMvc, dR);

        LocalDate votingDate = TimeProvider.now().toLocalDate();
        LocalTime openTime = TimeProvider.now().toLocalTime().plusMinutes(5).plusSeconds(5);
        LocalTime closeTime = openTime.plusMinutes(5);
        VotingEntity voting = addVotingInSejmCorrectly(mockMvc, vR, docID, votingDate, openTime.toString(), closeTime.toString());

        CountDownLatch time = new CountDownLatch(1);
        time.await(1, TimeUnit.SECONDS);
        // 3 - za 5 wstrzymaj sie 4 - przeciw
        CitizenEntity posel = cS.findByEmail("posel2@zp.pl").orElseThrow();
        assertThat(voteR.findByCitizenIdVotingId(posel.getCitizenID(), voting.getVotingID()).isEmpty()).isEqualTo(true);
        mockMvc.perform(post("/parlament/vote/"+voting.getVotingID())
                .param("votingRadio", "3")
                .with(user("posel2@zp.pl").roles("POSEL"))
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(status().reason(containsString("Voting has ended.")));

        Optional<VoteEntity> vote = voteR.findByCitizenID_CitizenIDAndVotingID_VotingID(posel.getCitizenID(), voting.getVotingID());
        assertThat(vote.isEmpty()).isEqualTo(true);

        vR.deleteById(voting.getVotingID());
        dR.deleteById(docID);
    }

    @Test
    void getOnPageBeforeClosing_votingAfterClosing() throws Exception {
        TimeProvider.useFixedClockAt(NOW);

        Long docID = addDocumentSejm(mockMvc, dR);

        LocalDate votingDate = TimeProvider.now().toLocalDate();
        LocalTime openTime = TimeProvider.now().toLocalTime();
        LocalTime closeTime = openTime.plusSeconds(1);
        VotingEntity voting = addVotingInSejmCorrectly(mockMvc, vR, docID, votingDate, openTime.toString(), closeTime.toString());
        mockMvc.perform(get("/parlament/vote/"+voting.getVotingID())
                .with(user("posel2@zp.pl").roles("POSEL")))
                .andExpect(status().isOk());

        CountDownLatch time = new CountDownLatch(1);
        time.await(1, TimeUnit.SECONDS);
        // 3 - za 5 wstrzymaj sie 4 - przeciw
        CitizenEntity posel = cS.findByEmail("posel2@zp.pl").orElseThrow();
        assertThat(voteR.findByCitizenIdVotingId(posel.getCitizenID(), voting.getVotingID()).isEmpty()).isEqualTo(true);
        mockMvc.perform(post("/parlament/vote/"+voting.getVotingID())
                .param("votingRadio", "3")
                .with(user("posel2@zp.pl").roles("POSEL"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Głosowanie już się zakończyło")));

        Optional<VoteEntity> vote = voteR.findByCitizenID_CitizenIDAndVotingID_VotingID(posel.getCitizenID(), voting.getVotingID());
        assertThat(vote.isEmpty()).isEqualTo(true);

        vR.deleteById(voting.getVotingID());
        dR.deleteById(docID);
    }

    @Test
    void repeatVote() throws Exception {
        TimeProvider.useFixedClockAt(NOW);

        Long docID = addDocumentSejm(mockMvc, dR);

        LocalDate votingDate = LocalDate.now();
        LocalTime openTime = LocalTime.now();
        LocalTime closeTime = openTime.plusMinutes(5);

        VotingEntity voting = addVotingInSejmCorrectly(mockMvc, vR, docID, votingDate, openTime.toString(), closeTime.toString());

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
                .andExpect(redirectedUrlPattern("/wydarzenie/*"));
        Optional<VoteEntity> vote = voteR.findByCitizenID_CitizenIDAndVotingID_VotingID(posel.getCitizenID(), voting.getVotingID());
        assertThat(vote.isPresent()).isEqualTo(true);

        mockMvc.perform(post("/parlament/vote/"+voting.getVotingID())
                .param("votingRadio", "3")
                .with(user("posel2@zp.pl").roles("POSEL"))
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(status().reason(containsString("You already send your vote")));

        assertThat(voteR.countAllByCitizenID_CitizenIDAndVotingID_VotingID(posel.getCitizenID(), voting.getVotingID())).isEqualTo(1);
        voteR.deleteById(vote.orElseThrow().getVoteID());
        vR.deleteById(voting.getVotingID());
        dR.deleteById(docID);
    }

}
