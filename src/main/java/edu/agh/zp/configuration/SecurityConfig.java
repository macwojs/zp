package edu.agh.zp.configuration;


import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.services.CitizenService;
import edu.agh.zp.services.citizenDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** @SecutityConfig
 * @apiNote Password encrypting, User authentication
 * Example: https://reflectoring.io/spring-security-password-handling/
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private citizenDetailsService cS;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf();
        http.httpBasic().disable();
        // TODO...Change the authorization
        http.authorizeRequests().antMatchers("/parlament").authenticated()
                .and()
                .formLogin()
                .loginPage("/signin")
                .loginProcessingUrl("signin.html")
                .defaultSuccessUrl("/");

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

}