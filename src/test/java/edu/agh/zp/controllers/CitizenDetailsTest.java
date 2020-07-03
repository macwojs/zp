package edu.agh.zp.controllers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import edu.agh.zp.services.CitizenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CitizenDetailsTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CitizenService cS;

    @Test
    void displayDetails() throws Exception {
        mockMvc.perform(get("/obywatel/dane").with(user("posel2@zp.pl")))
                .andExpect(content().string(containsString("Jan")))
                .andExpect(content().string(containsString("Kowalski")))
                .andExpect(content().string(containsString("Konstancin-Jeziorna")))
                .andExpect(content().string(containsString("al. Lipski 0761")))
                .andExpect(content().string(containsString("08030261521")))
                .andExpect(content().string(containsString("SOU105394")))
                .andExpect(content().string(containsString("posel2@zp.pl")));
    }

    @Test
    @Transactional
    void changeAddress() throws Exception {
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
                .with(csrf()))
                .andExpect(redirectedUrlPattern("**/"));

        mockMvc.perform(post("/obywatel/dane/modify/adr").with(user("user2@zp.pl"))
                .param("field1", "Nowa Sól")
                .param("field2", "ul. Akacjowa 25")
                .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/obywatel/dane").with(user("user2@zp.pl")))
                .andExpect(content().string(containsString("User")))
                .andExpect(content().string(containsString("User")))
                .andExpect(content().string(containsString("Nowa Sól")))
                .andExpect(content().string(containsString("ul. Akacjowa 25")))
                .andExpect(content().string(containsString("12242125054")))
                .andExpect(content().string(containsString("MKL818761")))
                .andExpect(content().string(containsString("user2@zp.pl")));

        assertThat( cS.findByEmail("user2@zp.pl").orElseThrow().getTown()).isEqualTo("Nowa Sól");
        assertThat( cS.findByEmail("user2@zp.pl").orElseThrow().getAddress()).isEqualTo("ul. Akacjowa 25");
    }


    @Test
    @Transactional
    void changeEmail() throws Exception {
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
                .with(csrf()))
                .andExpect(redirectedUrlPattern("**/"));

        mockMvc.perform(post("/obywatel/dane/modify/mail").with(user("user2@zp.pl").password("useruser"))
                .param("field1", "user3@zp.pl")
                .param("field2", "")
                .with(csrf()))
                .andExpect(redirectedUrl("/logout"));

        assertThat( cS.findByEmail("user2@zp.pl").isEmpty()).isEqualTo(true);
        assertThat( cS.findByEmail("user3@zp.pl").isPresent()).isEqualTo(true);

        mockMvc.perform((post("/signin")
                .param("email","user3@zp.pl")
                .param("password", "useruser")
                .with(csrf())))
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/"));

    }

    @Test
    @Transactional
    void changePassword() throws Exception {
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
                .with(csrf()))
                .andExpect(redirectedUrlPattern("**/"));
        String pass = cS.findByEmail("user@zp.pl").orElseThrow().getPassword();
        mockMvc.perform(post("/obywatel/dane/modify/pass").with(user("user@zp.pl"))
                .param("field1", "user1user1")
                .param("field2", "user1user1")
                .with(csrf()))
                .andExpect(redirectedUrl("/logout"));
    }

    @Test
    @Transactional
    void changePasswordValidation() throws Exception {
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
                .with(csrf()))
                .andExpect(redirectedUrlPattern("**/"));

        mockMvc.perform(post("/obywatel/dane/modify/pass").with(user("user2@zp.pl"))
                .param("field1", "user")
                .param("field2", "user")
                .with(csrf()))
                .andExpect(content().string(containsString("hasło musi zawierać conajmej 8 znaków")));
        mockMvc.perform(post("/obywatel/dane/modify/pass").with(user("user2@zp.pl"))
                .param("field1", "user1user1")
                .param("field2", "user2user2")
                .with(csrf()))
                .andExpect(content().string(containsString("hasła nie zgadzają się")));

    }
}
