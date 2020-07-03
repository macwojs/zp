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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
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
    private OptionRepository oR;

    @Autowired
    private OptionSetRepository osR;

    @Autowired
    private VoteRepository voteR;
    private static final LocalDateTime NEWNOW = LocalDateTime.now().plusDays(8).withHour(12);
    private static final LocalDateTime NOW = LocalDateTime.now();

    @Test
    void addVoteInReferendum() throws Exception {
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

    @Test
    void addVoteInElection() throws Exception {
        long before = vR.count();
        mockMvc.perform(post("/glosowania/prezydenckie/planAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("optionName1", "Anna Nowak")
                .param("optionName2", "Jan Kowalski")
                .param("optionName3", "Piotr Nowacki")
                .param("date", NEWNOW.toLocalDate().toString())
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/wydarzenie/*"));
        List<VotingEntity> list = vR.findAll();
        list.sort(SejmTest::compare);
        assertThat(list.size()).isEqualTo(before + 1);
        Optional<VotingEntity> voting = vR.findById(list.get((int) before).getVotingID());
        assertThat(voting.isPresent()).isEqualTo(true);
        VotingEntity election = voting.orElseThrow();


        TimeProvider.useFixedClockAt(NEWNOW);
        CountDownLatch time = new CountDownLatch(1);
        time.await(1, TimeUnit.SECONDS);
        // 1 - tak 2 nie
        Optional<CitizenEntity> user = cS.findByEmail("user@zp.pl");
        Optional<VotingControlEntity> votingControlBefore = votingControlSession.findByCitizenIDAndVotingID(user.orElseThrow(), election);
        assertThat(votingControlBefore.isPresent()).isEqualTo(false);
        List<OptionSetEntity> optionSet = osR.findAllBySetIDcolumn(election.getSetID_column());
        mockMvc.perform(post("/obywatel/wyboryReferenda/"+election.getVotingID())
                .param("votingRadio", String.valueOf(optionSet.get(1).getOptionID().getOptionID()))
                .with(user("user@zp.pl").roles("USER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/obywatel/wyboryReferenda"));

        Optional<VotingControlEntity> votingControlAfter = votingControlSession.findByCitizenIDAndVotingID(user.orElseThrow(), election);
        assertThat(votingControlAfter.isPresent()).isEqualTo(true);
        List<VoteEntity> votes = voteR.findAllByVotingID(election);
        votingControlSession.deleteByVotingID(election);
        voteR.deleteAll(votes);
        vR.deleteById(election.getVotingID());
    }
    @Test
    void addVoteInReferendumAsAnonymousUser() throws Exception {
        VotingEntity referendum = addReferendumAsMarszalekSejmu(NEWNOW.toLocalDate() ,vR ,mockMvc);
        TimeProvider.useFixedClockAt(NEWNOW);

        CountDownLatch time = new CountDownLatch(1);
        time.await(1, TimeUnit.SECONDS);
        // 1 - tak 2 nie
        mockMvc.perform(post("/obywatel/wyboryReferenda/"+referendum.getVotingID())
                .param("votingRadio", "1")
                .with(anonymous())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/signin"));
        List<VoteEntity> votes = voteR.findAllByVotingID(referendum);
        assertThat(votes.isEmpty()).isEqualTo(true);
        vR.deleteById(referendum.getVotingID());
    }

    @Test
    void addVoteInReferendumTimeOut1MinuteAfter() throws Exception {
        VotingEntity referendum = addReferendumAsMarszalekSejmu(NEWNOW.toLocalDate(),vR ,mockMvc);
        TimeProvider.useFixedClockAt(NEWNOW.withHour(21).withMinute(1));
        // 1 - tak 2 nie
        Optional<CitizenEntity> user = cS.findByEmail("user@zp.pl");
        Optional<VotingControlEntity> votingControlBefore = votingControlSession.findByCitizenIDAndVotingID(user.orElseThrow(), referendum);
        assertThat(votingControlBefore.isPresent()).isEqualTo(false);
        mockMvc.perform(post("/obywatel/wyboryReferenda/"+referendum.getVotingID())
                .param("votingRadio", "1")
                .with(user("user@zp.pl").roles("USER"))
                .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("Głosowanie już się zakończyło")));

        Optional<VotingControlEntity> votingControlAfter = votingControlSession.findByCitizenIDAndVotingID(user.orElseThrow(), referendum);
        assertThat(votingControlAfter.isPresent()).isEqualTo(false);
        List<VoteEntity> votes = voteR.findAllByVotingID(referendum);
        assertThat(votes.isEmpty()).isEqualTo(true);
        vR.deleteById(referendum.getVotingID());
    }

    @Test
    void addVoteInReferendumTimeOut1DayAfter() throws Exception {
        VotingEntity referendum = addReferendumAsMarszalekSejmu(NEWNOW.toLocalDate(),vR ,mockMvc);
        TimeProvider.useFixedClockAt(NEWNOW.plusDays(1));
        // 1 - tak 2 nie
        Optional<CitizenEntity> user = cS.findByEmail("user@zp.pl");
        Optional<VotingControlEntity> votingControlBefore = votingControlSession.findByCitizenIDAndVotingID(user.orElseThrow(), referendum);
        assertThat(votingControlBefore.isPresent()).isEqualTo(false);
        mockMvc.perform(post("/obywatel/wyboryReferenda/"+referendum.getVotingID())
                .param("votingRadio", "1")
                .with(user("user@zp.pl").roles("USER"))
                .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("Głosowanie już się zakończyło")));

        Optional<VotingControlEntity> votingControlAfter = votingControlSession.findByCitizenIDAndVotingID(user.orElseThrow(), referendum);
        assertThat(votingControlAfter.isPresent()).isEqualTo(false);
        List<VoteEntity> votes = voteR.findAllByVotingID(referendum);
        assertThat(votes.isEmpty()).isEqualTo(true);
        vR.deleteById(referendum.getVotingID());
    }

    @Test
    void addVoteInReferendum1DayBeforeOpening() throws Exception {
        VotingEntity referendum = addReferendumAsMarszalekSejmu(NEWNOW.toLocalDate(),vR ,mockMvc);
        TimeProvider.useFixedClockAt(NEWNOW.minusDays(1));
        // 1 - tak 2 nie
        Optional<CitizenEntity> user = cS.findByEmail("user@zp.pl");
        Optional<VotingControlEntity> votingControlBefore = votingControlSession.findByCitizenIDAndVotingID(user.orElseThrow(), referendum);
        assertThat(votingControlBefore.isPresent()).isEqualTo(false);
        mockMvc.perform(post("/obywatel/wyboryReferenda/"+referendum.getVotingID())
                .param("votingRadio", "1")
                .with(user("user@zp.pl").roles("USER"))
                .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("Głosowanie już się zakończyło")));

        Optional<VotingControlEntity> votingControlAfter = votingControlSession.findByCitizenIDAndVotingID(user.orElseThrow(), referendum);
        assertThat(votingControlAfter.isPresent()).isEqualTo(false);
        List<VoteEntity> votes = voteR.findAllByVotingID(referendum);
        assertThat(votes.isEmpty()).isEqualTo(true);
        vR.deleteById(referendum.getVotingID());
    }

    @Test
    void addVoteInReferendum1minuteBeforeOpening() throws Exception {
        VotingEntity referendum = addReferendumAsMarszalekSejmu(NEWNOW.toLocalDate(),vR ,mockMvc);
        TimeProvider.useFixedClockAt(NEWNOW.withHour(4).withMinute(59));
        // 1 - tak 2 nie
        Optional<CitizenEntity> user = cS.findByEmail("user@zp.pl");
        Optional<VotingControlEntity> votingControlBefore = votingControlSession.findByCitizenIDAndVotingID(user.orElseThrow(), referendum);
        assertThat(votingControlBefore.isPresent()).isEqualTo(false);
        mockMvc.perform(post("/obywatel/wyboryReferenda/"+referendum.getVotingID())
                .param("votingRadio", "1")
                .with(user("user@zp.pl").roles("USER"))
                .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("Głosowanie już się zakończyło")));

        Optional<VotingControlEntity> votingControlAfter = votingControlSession.findByCitizenIDAndVotingID(user.orElseThrow(), referendum);
        assertThat(votingControlAfter.isPresent()).isEqualTo(false);
        List<VoteEntity> votes = voteR.findAllByVotingID(referendum);
        assertThat(votes.isEmpty()).isEqualTo(true);
        vR.deleteById(referendum.getVotingID());
    }


    @Test
    void repeatVote() throws Exception {
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
        time.await(1, TimeUnit.SECONDS);
        mockMvc.perform(post("/obywatel/wyboryReferenda/"+referendum.getVotingID())
                .param("votingRadio", "1")
                .with(user("user@zp.pl").roles("USER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Już oddałeś głos w tym głosowaniu")));

        List<VoteEntity> votes = voteR.findAllByVotingID(referendum);
        assertThat(votes.size()).isEqualTo(1);
        votingControlSession.deleteByVotingID(referendum);
        voteR.deleteAll(votes);
        vR.deleteById(referendum.getVotingID());
    }
}
