package edu.agh.zp.controllers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.services.CitizenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SignUpTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CitizenService cS;

    @Test
    @Transactional
    void correctRegistration() throws Exception {
        assertThat( cS.findByEmail("user2@zp.pl").isEmpty() ).isEqualTo(true);
        mockMvc.perform(post("/signup")
                .param("email","user2@zp.pl")
                .param("password", "useruser")
                .param("repeatPassword", "useruser")
                .param("name", "User")
                .param("surname", "User")
                .param("town", "Krakow")
                .param("address", "ul. Twardowskiego 1")
                .param("pesel", "12242125054")
                .param("idNumber", "MKL818761")
                .with(csrf())).andExpect(redirectedUrlPattern("**/"));
        assertThat( cS.findByEmail("user2@zp.pl").isPresent() ).isEqualTo(true);
    }

    @Test
    @Transactional
    void thisEmailExists() throws Exception {
        assertThat( cS.findByEmail("user@zp.pl").isPresent() ).isEqualTo(true);
        mockMvc.perform(post("/signup")
                .param("email","user@zp.pl")
                .param("password", "useruser")
                .param("repeatPassword", "useruser")
                .param("name", "User")
                .param("surname", "User")
                .param("town", "Krakow")
                .param("address", "ul. Twardowskiego 1")
                .param("pesel", "51032410752")
                .param("idNumber", "NMR475231")
                .with(csrf()))
                .andExpect(redirectedUrl(null))
                .andExpect(content().string(containsString("Obywatel z tym adresem e-mail już istnieje.")));
        Optional<CitizenEntity> citizen = cS.findByEmail("user@zp.pl");
        assertThat( citizen.isPresent() ).isEqualTo(true);
        assertThat( citizen.orElseThrow().getPesel()).isEqualTo("00010133000");
    }

    @Test
    @Transactional
    void thisPeselExists() throws Exception {
        assertThat( cS.findByPesel("00010133000").isPresent() ).isEqualTo(true);
        mockMvc.perform(post("/signup")
                .param("email","user2@zp.pl")
                .param("password", "useruser")
                .param("repeatPassword", "useruser")
                .param("name", "User")
                .param("surname", "User")
                .param("town", "Krakow")
                .param("address", "ul. Twardowskiego 1")
                .param("pesel", "00010133000")
                .param("idNumber", "NMR475231")
                .with(csrf()))
                .andExpect(redirectedUrl(null))
                .andExpect(content().string(containsString("Obywatel z tym numerem PESEL już istnieje.")));
        Optional<CitizenEntity> citizen = cS.findByPesel("00010133000");
        assertThat( citizen.isPresent() ).isEqualTo(true);
        assertThat( citizen.orElseThrow().getEmail()).isEqualTo("user@zp.pl");
    }

    @Test
    @Transactional
    void thisIdNumberExists() throws Exception {
        assertThat( cS.findByIdNumer("AOS266716").isPresent() ).isEqualTo(true);
        mockMvc.perform(post("/signup")
                .param("email","user2@zp.pl")
                .param("password", "useruser")
                .param("repeatPassword", "useruser")
                .param("name", "User")
                .param("surname", "User")
                .param("town", "Krakow")
                .param("address", "ul. Twardowskiego 1")
                .param("pesel", "12242125054")
                .param("idNumber", "AOS266716")
                .with(csrf()))
                .andExpect(redirectedUrl(null))
                .andExpect(content().string(containsString("Obywatel z tym numerem dowodu osobistego już istnieje.")));
        Optional<CitizenEntity> citizen = cS.findByIdNumer("AOS266716");
        assertThat( citizen.isPresent() ).isEqualTo(true);
        assertThat( citizen.orElseThrow().getPesel()).isEqualTo("00010133000");
    }

    @Test
    @Transactional
    void wrongFormatOfPeselAndIdNumber() throws Exception {
        assertThat( cS.findByIdNumer("user2@zp.pl").isEmpty() ).isEqualTo(true);
        mockMvc.perform(post("/signup")
                .param("email","user1@zp.pl")
                .param("password", "useruser")
                .param("repeatPassword", "useruser")
                .param("name", "User")
                .param("surname", "User")
                .param("town", "Krakow")
                .param("address", "ul. Twardowskiego 1")
                .param("pesel", "342554562425")
                .param("idNumber", "532352354vcvc")
                .with(csrf()))
                .andExpect(redirectedUrl(null))
                .andExpect(content().string(containsString("Pesel musi posiadać 11 cyfr.")))
                .andExpect(content().string(containsString("Wprowadź poprawny numer pesel.")))
                .andExpect(content().string(containsString("Wprowadz numer dowodu w formacie ABC123456.")))
                .andExpect(content().string(containsString("Wprowadź poprawny numer dowodu osobistego.")));
        assertThat( cS.findByIdNumer("user1@zp.pl").isEmpty() ).isEqualTo(true);
    }

    @Test
    @Transactional
    void wrongPeselAndIdNumber() throws Exception {
        assertThat( cS.findByIdNumer("user2@zp.pl").isEmpty() ).isEqualTo(true);
        mockMvc.perform(post("/signup")
                .param("email","user1@zp.pl")
                .param("password", "useruser")
                .param("repeatPassword", "useruser")
                .param("name", "User")
                .param("surname", "User")
                .param("town", "Krakow")
                .param("address", "ul. Twardowskiego 1")
                .param("pesel", "01928374654")
                .param("idNumber", "ABC123456")
                .with(csrf()))
                .andExpect(redirectedUrl(null))
                .andExpect(content().string(containsString("Wprowadź poprawny numer pesel.")))
                .andExpect(content().string(containsString("Wprowadź poprawny numer dowodu osobistego.")));
        assertThat( cS.findByIdNumer("user1@zp.pl").isEmpty() ).isEqualTo(true);
    }

    @Test
    @Transactional
    void tooShortPassword() throws Exception {
        assertThat( cS.findByIdNumer("user2@zp.pl").isEmpty() ).isEqualTo(true);
        mockMvc.perform(post("/signup")
                .param("email","user2@zp.pl")
                .param("password", "user")
                .param("repeatPassword", "user")
                .param("name", "User")
                .param("surname", "User")
                .param("town", "Krakow")
                .param("address", "ul. Twardowskiego 1")
                .param("pesel", "12242125054")
                .param("idNumber", "MKL818761")
                .with(csrf()))
                .andExpect(redirectedUrl(null))
                .andExpect(content().string(containsString("Hasło musi posiadać minimum 8 znaków")));
        assertThat( cS.findByIdNumer("user2@zp.pl").isEmpty() ).isEqualTo(true);
    }


    @Test
    @Transactional
    void passwordAndRPasswordNotMatch() throws Exception {
        assertThat( cS.findByIdNumer("user2@zp.pl").isEmpty() ).isEqualTo(true);
        mockMvc.perform(post("/signup")
                .param("email","user1@zp.pl")
                .param("password", "useruser")
                .param("repeatPassword", "userresu")
                .param("name", "User")
                .param("surname", "User")
                .param("town", "Krakow")
                .param("address", "ul. Twardowskiego 1")
                .param("pesel", "12242125054")
                .param("idNumber", "MKL818761")
                .with(csrf()))
                .andExpect(redirectedUrl(""));
        //        .andExpect(content().string(containsString("Hasła się nie zgadzają)));
        assertThat( cS.findByIdNumer("user2@zp.pl").isEmpty() ).isEqualTo(true);
    }
}
