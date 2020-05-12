package edu.agh.zp.configuration;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.Role;
import edu.agh.zp.repositories.RoleRepository;
import edu.agh.zp.services.CitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
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
    }

    private void createSampleAdmin(String email, String password, String name, String surname, String pesel, String idnumber){
        CitizenEntity admin=  new CitizenEntity(password,password, email, name, surname,pesel, idnumber);
        admin.setPassword(BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt()));
        setRole(admin, "ROLE_ADMIN");
        cS.create(admin);
    }

    private void createSampleUser(String email, String password, String name, String surname, String pesel, String idnumber){
        CitizenEntity admin=  new CitizenEntity(password,password, email, name, surname,pesel, idnumber);
        admin.setPassword(BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt()));
        setRole(admin, "ROLE_USER");
        cS.create(admin);
    }

    private void setRole(CitizenEntity citizen, String role){
        List<Role> roles = new ArrayList<Role>();
        Optional<Role> temp = rR.findByName(role);
        temp.ifPresent(roles::add);
        citizen.setRoles(roles);
    }
}
