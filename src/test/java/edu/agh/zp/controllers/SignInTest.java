package edu.agh.zp.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.stream;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import edu.agh.zp.controller.SignInController;
import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.services.CitizenService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SignInTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;


    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }


    @Test
    public void correctLoginCredentialsTest() throws Exception {
        mockMvc.perform(get("/signin")).andExpect(cookie().exists("OLD_URL_REDIRECT"));
        mockMvc.perform((post("/signin")
                .param("email","user@zp.pl")
                .param("password", "useruser")
                .with(csrf()))
        ).andExpect(authenticated());
    }

    @Test
    public void badLoginCredentialsTest() throws Exception {
//        mockMvc.perform(post("/signin")
//                .param("email","user")
//                .param("password", "useruser")
//                .with(csrf())
//        );
        mockMvc.perform(get("/").with(anonymous())).andExpect(unauthenticated());
        mockMvc.perform(post("/signin")
                .param("email","user@zp.pl")
                .param("password", "user1user")
                .with(csrf())
        ).andExpect(unauthenticated());
    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(get("/parlament/documentForm")).andExpect(unauthenticated());
        mockMvc.perform((post("/signin")
                .param("email","marszaleksejmu@zp.pl")
                .param("password", "marszalekmarszalek")
                .with(csrf()))
        ).andExpect(authenticated().withUsername("marszaleksejmu@zp.pl"));
        mockMvc.perform(logout()).andExpect(unauthenticated());
        mockMvc.perform(get("/parlament/documentForm")).andExpect(unauthenticated());
    }

    @Test
    public void testUserAuthentication() throws Exception {
        mockMvc.perform(post("/parlament/documentForm").with(user("user@zp.pl").roles("USER"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/glosowania/zmianaDaty/1").with(user("user@zp.pl").roles("USER"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/parlament/vote/1").with(user("user@zp.pl").roles("USER"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/parlament/vote/zmianaDaty/**").with(user("user@zp.pl").roles("USER"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/parlament/senat/voteAdd").with(user("user@zp.pl").roles("USER"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/parlament/sejm/voteAdd").with(user("user@zp.pl").roles("USER"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/prezydent").with(user("user@zp.pl").roles("USER"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/ustawy/status/**").with(user("user@zp.pl").roles("USER"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/ustawy/description/**").with(user("user@zp.pl").roles("USER"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/ustawy/annotation/**").with(user("user@zp.pl").roles("USER"))).andExpect(status().isForbidden());
    }

    @Test
    public void testAnonymousAccess() throws Exception {
        mockMvc.perform(post("/parlament/documentForm").with(anonymous())).andExpect(status().isForbidden());
        mockMvc.perform(get("/glosowania/zmianaDaty/**").with(anonymous())).andExpect(redirectedUrlPattern("**/signin"));
        mockMvc.perform(get("/parlament/vote/**").with(anonymous())).andExpect(redirectedUrlPattern("**/signin"));
        mockMvc.perform(get("/parlament/vote/zmianaDaty/**").with(anonymous())).andExpect(redirectedUrlPattern("**/signin"));
        mockMvc.perform(get("/parlament/senat/voteAdd").with(anonymous())).andExpect(redirectedUrlPattern("**/signin"));
        mockMvc.perform(get("/parlament/sejm/voteAdd").with(anonymous())).andExpect(redirectedUrlPattern("**/signin"));
        mockMvc.perform(get("/prezydent").with(anonymous())).andExpect(redirectedUrlPattern("**/signin"));
        mockMvc.perform(get("/ustawy/status/**").with(anonymous())).andExpect(redirectedUrlPattern("**/signin"));
        mockMvc.perform(get("/ustawy/description/**").with(anonymous())).andExpect(redirectedUrlPattern("**/signin"));
        mockMvc.perform(get("/ustawy/annotation/**").with(anonymous())).andExpect(redirectedUrlPattern("**/signin"));
        mockMvc.perform(get("/obywatel/**").with(anonymous())).andExpect(redirectedUrlPattern("**/signin"));
        mockMvc.perform(get("/").with(anonymous())).andExpect(unauthenticated()).andExpect(status().isOk());
        mockMvc.perform(get("/wydarzenie/kalendarz").with(anonymous())).andExpect(unauthenticated()).andExpect(status().isOk());
        mockMvc.perform(get("/ustawy/dziennikUstaw").with(anonymous())).andExpect(unauthenticated()).andExpect(status().isOk());
    }

    @Test
    public void testMarszalekSejmuAuthentication() throws Exception {
        mockMvc.perform(get("/parlament/documentForm").with(user("user@zp.pl").roles("MARSZALEK_SEJMU"))).andExpect(authenticated().withRoles("MARSZALEK_SEJMU")).andExpect(status().isOk());
        mockMvc.perform(get("/glosowania/prezydenckie/plan").with(user("user@zp.pl").roles("MARSZALEK_SEJMU"))).andExpect(authenticated().withRoles("MARSZALEK_SEJMU")).andExpect(status().isOk());
        mockMvc.perform(get("/glosowania/referendum/plan").with(user("user@zp.pl").roles("MARSZALEK_SEJMU"))).andExpect(authenticated().withRoles("MARSZALEK_SEJMU")).andExpect(status().isOk());
        mockMvc.perform(get("/parlament/senat/voteAdd").with(user("user@zp.pl").roles("MARSZALEK_SEJMU"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/parlament/sejm/voteAdd").with(user("user@zp.pl").roles("MARSZALEK_SEJMU"))).andExpect(authenticated().withRoles("MARSZALEK_SEJMU")).andExpect(status().isOk());
        mockMvc.perform(get("/prezydent").with(user("user@zp.pl").roles("MARSZALEK_SEJMU"))).andExpect(status().isForbidden());
    }
    @Test
    public void testMarszalekSenatuAuthentication() throws Exception {
        mockMvc.perform(get("/parlament/documentForm").with(user("user@zp.pl").roles("MARSZALEK_SENATU"))).andExpect(authenticated().withRoles("MARSZALEK_SENATU")).andExpect(status().isOk());
        mockMvc.perform(get("/glosowania/prezydenckie/plan").with(user("user@zp.pl").roles("MARSZALEK_SENATU"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/glosowania/referendum/plan").with(user("user@zp.pl").roles("MARSZALEK_SENATU"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/parlament/senat/voteAdd").with(user("user@zp.pl").roles("MARSZALEK_SENATU"))).andExpect(authenticated().withRoles("MARSZALEK_SENATU")).andExpect(status().isOk());
        mockMvc.perform(get("/parlament/sejm/voteAdd").with(user("user@zp.pl").roles("MARSZALEK_SENATU"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/prezydent").with(user("user@zp.pl").roles("MARSZALEK_SENATU"))).andExpect(status().isForbidden());
    }

    @Test
    public void testPrezydentAuthentication() throws Exception {
        mockMvc.perform(get("/prezydent/podpisz").with(user("user@zp.pl").roles("PREZYDENT"))).andExpect(authenticated().withRoles("PREZYDENT")).andExpect(status().isOk());
        mockMvc.perform(get("/glosowania/prezydenckie/plan").with(user("user@zp.pl").roles("PREZYDENT"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/glosowania/referendum/plan").with(user("user@zp.pl").roles("PREZYDENT"))).andExpect(authenticated().withRoles("PREZYDENT")).andExpect(status().isOk());
    }
}
