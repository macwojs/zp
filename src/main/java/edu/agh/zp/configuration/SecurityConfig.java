package edu.agh.zp.configuration;

import edu.agh.zp.services.citizenDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/** SecutityConfig
 * @apiNote Password encrypting, User authentication
 * Example: https://reflectoring.io/spring-security-password-handling/
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private citizenDetailsService cS;
    @Autowired
    private DataSource dataSource;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf();
        http.httpBasic().disable();
        // TODO...Change the authorization
        http.authorizeRequests()
                .antMatchers( "/obywatel/**" ).authenticated()

                .antMatchers("/glosowania/prezydenckie/**").hasAnyRole("ADMIN", "MARSZALEK_SEJMU")
                .antMatchers("/glosowania/referendum/**").hasAnyRole("ADMIN", "MARSZALEK_SEJMU", "PREZYDENT")
                .antMatchers("/glosowania/zmianaDaty/**").hasAnyRole("ADMIN", "MARSZALEK_SEJMU", "PREZYDENT")

                .antMatchers("/parlament/documentForm").hasAnyRole("ADMIN", "MARSZALEK_SENATU", "MARSZALEK_SEJMU")
                .antMatchers("/parlament/vote/**").hasAnyRole("ADMIN", "SENATOR", "POSEL")
                .antMatchers("/parlament/vote/zmianaDaty/**").hasAnyRole("ADMIN", "MARSZALEK_SENATU", "MARSZALEK_SEJMU")

                .antMatchers("/parlament/senat/voteAdd").hasAnyRole("MARSZALEK_SENATU", "ADMIN")

                .antMatchers("/parlament/sejm/voteAdd").hasAnyRole("MARSZALEK_SEJMU", "ADMIN")

                .antMatchers("/prezydent").hasAnyRole("ADMIN", "PREZYDENT")

                .antMatchers("/ustawy/status/**").hasAnyRole("ADMIN", "MARSZALEK_SEJMU", "MARSZALEK_SENATU")
                .antMatchers("/ustawy/description/**").hasAnyRole("ADMIN", "MARSZALEK_SEJMU", "MARSZALEK_SENATU", "SENATOR", "POSEL")
                .antMatchers("/ustawy/annotation/**").hasAnyRole("MARSZALEK_SEJMU", "MARSZALEK_SENATU","ADMIN", "SENATOR", "POSEL")

                .and()
                .formLogin()
                .loginPage("/signin")
                .loginProcessingUrl("signin.html")
                .defaultSuccessUrl("/")
                .and()
                .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID");

        http.authorizeRequests().antMatchers( "/static/**","/resources/**", "/js/**", "/css/**", "/img/**").permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth ) throws Exception {
        auth.userDetailsService(cS);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl tokenRepositoryImpl = new JdbcTokenRepositoryImpl();
        tokenRepositoryImpl.setDataSource(dataSource);
        return tokenRepositoryImpl;
    }
}

