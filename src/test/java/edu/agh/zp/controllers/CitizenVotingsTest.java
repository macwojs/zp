package edu.agh.zp.controllers;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import edu.agh.zp.objects.VotingEntity;
import edu.agh.zp.repositories.VotingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CitizenVotingsTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VotingRepository vR;

    @Test
    void addReferendumByPrezydent() throws Exception {
        LocalDate referendumDate = LocalDate.now().plusDays(30);
        mockMvc.perform(post("/glosowania/referendum/planAdd")
                        .with(user("prezydent@zp.pl").roles("PREZYDENT"))
                        .param("desc", "Czy każdy powinien dostać darmowe ciastka?")
                        .param("date", referendumDate.toString())
                        .with(csrf()))
                        .andExpect(redirectedUrlPattern("/wydarzenie/*"));
        List<VotingEntity> list = vR.findAll();
        Optional<VotingEntity> voting = vR.findById(list.get(list.size()-1).getVotingID());
        assertThat( voting.orElseThrow().getVotingDescription() ).isEqualTo("Czy każdy powinien dostać darmowe ciastka?");
        vR.deleteById(list.get(list.size()-1).getVotingID());
    }

    @Test
    void addReferendumByMarszalekSejmu() throws Exception {
        LocalDate referendumDate = LocalDate.now().plusDays(30);
        mockMvc.perform(post("/glosowania/referendum/planAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("desc", "Czy każdy powinien dostać darmowe ciastka?")
                .param("date", referendumDate.toString())
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/wydarzenie/*"));
        List<VotingEntity> list = vR.findAll();
        Optional<VotingEntity> voting = vR.findById(list.get(list.size()-1).getVotingID());
        assertThat( voting.orElseThrow().getVotingDescription() ).isEqualTo("Czy każdy powinien dostać darmowe ciastka?");
        vR.deleteById(list.get(list.size()-1).getVotingID());
    }

    @Test
    void addReferendumWithWrongDate() throws Exception {
        LocalDate referendumDate = LocalDate.now().plusDays(2);
        long votingNumberBefore = vR.count();
        mockMvc.perform(post("/glosowania/referendum/planAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("desc", "Czy każdy powinien dostać darmowe ciastka?")
                .param("date", referendumDate.toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Wydarzenie musi być zaplanowane z 7 dniowym wyprzedzeniem")));
        long votingNumberAfter = vR.count();
        assertThat(votingNumberBefore).isEqualTo(votingNumberAfter);
    }

    @Test
    void addReferendumWithoutQuestion() throws Exception {
        LocalDate referendumDate = LocalDate.now().plusDays(30);
        long votingNumberBefore = vR.count();
        mockMvc.perform(post("/glosowania/referendum/planAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("desc", "")
                .param("date", referendumDate.toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Należy wpisać treść pytania")));
        long votingNumberAfter = vR.count();
        assertThat(votingNumberBefore).isEqualTo(votingNumberAfter);
    }

    @Test
    void addCitizenVoting() throws Exception {
        LocalDate votingDate = LocalDate.now().plusDays(30);
        mockMvc.perform(post("/glosowania/prezydenckie/planAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("optionName1", "Anna Nowak")
                .param("optionName2", "Jan Kowalski")
                .param("optionName3", "Piotr Nowacki")
                .param("date", votingDate.toString())
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/wydarzenie/*"));
        List<VotingEntity> list = vR.findAll();
        Optional<VotingEntity> voting = vR.findById(list.get(list.size()-1).getVotingID());
        assertThat( voting.orElseThrow().getVotingDescription() ).isEqualTo("Wybory Prezydenckie " + votingDate.toString());
        vR.deleteById(list.get(list.size()-1).getVotingID());
    }

    @Test
    void addVotingOnlyOneCandidate() throws Exception {
        LocalDate votingDate = LocalDate.now().plusDays(30);
        long votingNumberBefore = vR.count();
        mockMvc.perform(post("/glosowania/prezydenckie/planAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("optionName1", "Anna Nowak")
                .param("date", votingDate.toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("musisz podać przynajmnej 2 kandydatów")));
        long votingNumberAfter = vR.count();
        assertThat(votingNumberBefore).isEqualTo(votingNumberAfter);
    }

    @Test
    void addVotingEmptyCandidate() throws Exception {
        LocalDate votingDate = LocalDate.now().plusDays(30);
        long votingNumberBefore = vR.count();
        mockMvc.perform(post("/glosowania/prezydenckie/planAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("optionName1", "Anna Nowak")
                .param("optionName2", "")
                .param("date", votingDate.toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("dane kandydata nie mogą być puste")));
        long votingNumberAfter = vR.count();
        assertThat(votingNumberBefore).isEqualTo(votingNumberAfter);
    }

    @Test
    void addVotingWithWrongDate() throws Exception {
        LocalDate votingDate = LocalDate.now().plusDays(3);
        long votingNumberBefore = vR.count();
        mockMvc.perform(post("/glosowania/prezydenckie/planAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("optionName1", "Anna Nowak")
                .param("optionName2", "")
                .param("date", votingDate.toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("wydarzenie musi być zaplanowane z 7 dniowym wyprzedzeniem")));
        long votingNumberAfter = vR.count();
        assertThat(votingNumberBefore).isEqualTo(votingNumberAfter);
    }

    @Test
    void changeVotingDate() throws Exception {
        LocalDate votingDate = LocalDate.now().plusDays(30);
        mockMvc.perform(post("/glosowania/prezydenckie/planAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("optionName1", "Anna Nowak")
                .param("optionName2", "Jan Kowalski")
                .param("optionName3", "Piotr Nowacki")
                .param("date", votingDate.toString())
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/wydarzenie/*"));
        List<VotingEntity> list = vR.findAll();
        Optional<VotingEntity> votingTempBefore = vR.findById(list.get(list.size() - 1).getVotingID());
        VotingEntity voting = votingTempBefore.orElseThrow();
        assertThat(voting.getVotingDescription()).isEqualTo("Wybory Prezydenckie " + votingDate.toString());

        LocalDate votingDateAfter = LocalDate.now().plusDays(40);
        mockMvc.perform(get("/glosowania/zmianaDaty/" + voting.getVotingID() + "?dateForm=" + votingDateAfter.toString().substring(0, 10))
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU")))
                .andExpect(redirectedUrlPattern("/wydarzenie/*"));
        Optional<VotingEntity> votingTempAfter = vR.findById(voting.getVotingID());
        assertThat(votingDate.plusDays(10).toString()).isEqualTo(votingDateAfter.toString());
        assertThat(votingTempAfter.orElseThrow().getVotingDate()).isEqualTo(votingDateAfter.toString());
        vR.deleteById(voting.getVotingID());
    }

    @Test
    void changeVotingWithWrongDate() throws Exception {
        LocalDate votingDate = LocalDate.now().plusDays(30);
        mockMvc.perform(post("/glosowania/prezydenckie/planAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("optionName1", "Anna Nowak")
                .param("optionName2", "Jan Kowalski")
                .param("optionName3", "Piotr Nowacki")
                .param("date", votingDate.toString())
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/wydarzenie/*"));
        List<VotingEntity> list = vR.findAll();
        Optional<VotingEntity> votingTempBefore = vR.findById(list.get(list.size() - 1).getVotingID());
        VotingEntity voting = votingTempBefore.orElseThrow();
        assertThat(voting.getVotingDescription()).isEqualTo("Wybory Prezydenckie " + votingDate.toString());
        LocalDate votingDateAfter = LocalDate.now().plusDays(3);
        mockMvc.perform(get("/glosowania/zmianaDaty/" + voting.getVotingID() + "?dateForm=" + votingDateAfter.toString().substring(0, 10))
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU")))
                .andExpect(content().string(containsString("Głosowanie może być najwcześniej za 7 dni")));
        Optional<VotingEntity> votingTempAfter = vR.findById(voting.getVotingID());
        assertThat(votingTempAfter.orElseThrow().getVotingDate()).isEqualTo(votingDate.toString());
        vR.deleteById(voting.getVotingID());
    }

    @Test
    void changeVotingToYesterday() throws Exception {
        LocalDate votingDate = LocalDate.now().plusDays(30);
        mockMvc.perform(post("/glosowania/prezydenckie/planAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("optionName1", "Anna Nowak")
                .param("optionName2", "Jan Kowalski")
                .param("optionName3", "Piotr Nowacki")
                .param("date", votingDate.toString())
                .with(csrf()))
                .andExpect(redirectedUrlPattern("/wydarzenie/*"));
        List<VotingEntity> list = vR.findAll();
        Optional<VotingEntity> votingTempBefore = vR.findById(list.get(list.size() - 1).getVotingID());
        VotingEntity voting = votingTempBefore.orElseThrow();
        assertThat(voting.getVotingDescription()).isEqualTo("Wybory Prezydenckie " + votingDate.toString());
        LocalDate votingDateAfter = LocalDate.now().minusDays(1);
        mockMvc.perform(get("/glosowania/zmianaDaty/" + voting.getVotingID() + "?dateForm=" + votingDateAfter.toString().substring(0, 10))
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU")))
                .andExpect(content().string(containsString("Głosowanie może być najwcześniej za 7 dni")));
        Optional<VotingEntity> votingTempAfter = vR.findById(voting.getVotingID());
        assertThat(votingTempAfter.orElseThrow().getVotingDate()).isEqualTo(votingDate.toString());
        vR.deleteById(voting.getVotingID());
    }

}
