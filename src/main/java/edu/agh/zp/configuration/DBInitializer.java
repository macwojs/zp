package edu.agh.zp.configuration;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.Role;
import edu.agh.zp.repositories.RoleRepository;
import edu.agh.zp.services.CitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "app.db-init", havingValue= "true")
public class DBInitializer implements CommandLineRunner {
    @Autowired
    private CitizenService cS;
    @Autowired
    private RoleRepository rR;
    @Override
    public void run(String... args) throws Exception {
        createSampleAdmin("admin@zp.pl", "adminadmin","Admin", "Admin", "00010100190", "ARY546695");
        createSampleUser("user@zp.pl", "useruser","User", "User", "00010133000", "AOS266716");
        createSampleMarszalekSejmu("marszaleksejmu@zp.pl", "marszalekmarszalek","Marszalek", "Sejmu", "00010102260", "AEX304345");
    }

    private void createSampleAdmin(String email, String password, String name, String surname, String pesel, String idnumber){
        CitizenEntity admin=  createBasicUser(email, password, name, surname,pesel, idnumber);
        String[] roles =  {"ROLE_ADMIN"};
        setRole(admin,roles);
        cS.create(admin);
    }

    private void createSampleUser(String email, String password, String name, String surname, String pesel, String idnumber){
        CitizenEntity user=  createBasicUser(email, password, name, surname,pesel, idnumber);
        String[] roles =  {"ROLE_USER"};
        setRole(user, roles);
        cS.create(user);
    }

    private void createSampleMarszalekSejmu(String email, String password, String name, String surname, String pesel, String idnumber){
        CitizenEntity marszalek=  createBasicUser(email, password, name, surname,pesel, idnumber);
        String[] roles =  {"ROLE_USER", "ROLE_MARSZALEK_SEJMU", "ROLE_POSEL"};
        setRole(marszalek, roles);
        cS.create(marszalek);
    }

    private void createSampleMarszalekSenatu(String email, String password, String name, String surname, String pesel, String idnumber){
        CitizenEntity marszalek=  createBasicUser(email, password, name, surname,pesel, idnumber);
        String[] roles =  {"ROLE_USER", "ROLE_MARSZALEK_SENATU", "ROLE_SENATOR"};
        setRole(marszalek, roles);
        cS.create(marszalek);
    }

    private void createSampleSenator(String email, String password, String name, String surname, String pesel, String idnumber){
        CitizenEntity marszalek=  createBasicUser(email, password, name, surname,pesel, idnumber);
        String[] roles =  {"ROLE_USER", "ROLE_SENATOR"};
        setRole(marszalek, roles);
        cS.create(marszalek);
    }

    private void createSamplePosel(String email, String password, String name, String surname, String pesel, String idnumber){
        CitizenEntity marszalek=  createBasicUser(email, password, name, surname,pesel, idnumber);
        String[] roles =  {"ROLE_USER", "ROLE_POSEL"};
        setRole(marszalek, roles);
        cS.create(marszalek);
    }

    private void setRole(CitizenEntity citizen, String[] roles){
        List<Role> r = new ArrayList<Role>();
        for ( String role : roles ) {
            Optional<Role> temp = rR.findByName(role);
            temp.ifPresent(r::add);
        }
        citizen.setRoles(r);
    }

    private CitizenEntity createBasicUser(String email, String password, String name, String surname, String pesel, String idnumber){
        CitizenEntity user=  new CitizenEntity(password,password, email, name, surname,pesel, idnumber);
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        return user;
    }
}
