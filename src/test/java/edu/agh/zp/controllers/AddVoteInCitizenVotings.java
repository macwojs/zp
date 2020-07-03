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

import static edu.agh.zp.controllers.CitizenVotingsTest.addReferendumAsMarszalekSejmu;
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
public class AddVoteInCitizenVotings {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CitizenService cS;

    @Autowired
    private VotingRepository vR;

    @Autowired
    private VotingControlRepository votingControlSession;

    @Autowired
    private VoteRepository voteR;
    private static final LocalDateTime NEWNOW = LocalDateTime.now().plusDays(8).withHour(12);
    private static final LocalDateTime NOW = LocalDateTime.now();

    @Test
    void addVoteInRefeferndum() throws Exception {
        VotingEntity referendum = addReferendumAsMarszalekSejmu(NEWNOW.toLocalDate() ,vR ,mockMvc);
        TimeProvider.useFixedClockAt(NEWNOW);

        CountDownLatch time = new CountDownLatch(1);
        time.await(1, TimeUnit.SECONDS);
        // 1 - tak 2 nie
        Optional<CitizenEntity> user = cS.findByEmail("user@zp.pl");
        Optional<VotingControlEntity> votingControlBefore = votingControlSession.findByCitizenIDAndVotingID(user.orElseThrow(), referendum);
        assertThat(votingControlBefore.isPresent()).isEqualTo(false);
        mockMvc.perform(post("/obywatel/wyboryReferenda/"+referendum.getVotingID())
                .param("votingRadio", "1")
                .with(user("user@zp.pl").roles("USER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/obywatel/wyboryReferenda"));

        Optional<VotingControlEntity> votingControlAfter = votingControlSession.findByCitizenIDAndVotingID(user.orElseThrow(), referendum);
        assertThat(votingControlAfter.isPresent()).isEqualTo(true);
        List<VoteEntity> votes = voteR.findAllByVotingID(referendum);
        votingControlSession.deleteByVotingID(referendum);
        voteR.deleteAll(votes);
        vR.deleteById(referendum.getVotingID());
    }
}
