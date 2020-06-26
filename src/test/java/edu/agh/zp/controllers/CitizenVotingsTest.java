package edu.agh.zp.controllers;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        List<VotingEntity> listBefore = vR.findAll();
        mockMvc.perform(post("/glosowania/referendum/planAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("desc", "Czy każdy powinien dostać darmowe ciastka?")
                .param("date", referendumDate.toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Wydarzenie musi być zaplanowane z 7 dniowym wyprzedzeniem")));
        List<VotingEntity> listAfter = vR.findAll();
        assertThat(listAfter.size()).isEqualTo(listBefore.size());
    }

    @Test
    void addReferendumWithoutQuestion() throws Exception {
        LocalDate referendumDate = LocalDate.now().plusDays(30);
        List<VotingEntity> listBefore = vR.findAll();
        mockMvc.perform(post("/glosowania/referendum/planAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("desc", "")
                .param("date", referendumDate.toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Należy wpisać treść pytania")));
        List<VotingEntity> listAfter = vR.findAll();
        assertThat(listAfter.size()).isEqualTo(listBefore.size());
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
        List<VotingEntity> listBefore = vR.findAll();
        mockMvc.perform(post("/glosowania/prezydenckie/planAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("optionName1", "Anna Nowak")
                .param("date", votingDate.toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("musisz podać przynajmnej 2 kandydatów")));
        List<VotingEntity> listAfter = vR.findAll();
        assertThat(listAfter.size()).isEqualTo(listBefore.size());
    }

    @Test
    void addVotingEmptyCandidate() throws Exception {
        LocalDate votingDate = LocalDate.now().plusDays(30);
        List<VotingEntity> listBefore = vR.findAll();
        mockMvc.perform(post("/glosowania/prezydenckie/planAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("optionName1", "Anna Nowak")
                .param("optionName2", "")
                .param("date", votingDate.toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("dane kandydata nie mogą być puste")));
        List<VotingEntity> listAfter = vR.findAll();
        assertThat(listAfter.size()).isEqualTo(listBefore.size());
    }

    @Test
    void addVotingWithWrongDate() throws Exception {
        LocalDate votingDate = LocalDate.now().plusDays(3);
        List<VotingEntity> listBefore = vR.findAll();
        mockMvc.perform(post("/glosowania/prezydenckie/planAdd")
                .with(user("marszaleksejmu@zp.pl").roles("MARSZALEK_SEJMU"))
                .param("optionName1", "Anna Nowak")
                .param("optionName2", "")
                .param("date", votingDate.toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("wydarzenie musi być zaplanowane z 7 dniowym wyprzedzeniem")));
        List<VotingEntity> listAfter = vR.findAll();
        assertThat(listAfter.size()).isEqualTo(listBefore.size());
    }
}
