package edu.agh.zp.configuration;

import com.github.javafaker.Faker;
import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.ParliamentarianEntity;
import edu.agh.zp.objects.PoliticianEntity;
import edu.agh.zp.objects.Role;
import edu.agh.zp.repositories.PoliticianRepository;
import edu.agh.zp.repositories.RoleRepository;
import edu.agh.zp.services.CitizenService;
import edu.agh.zp.services.ParliamentarianService;
import edu.agh.zp.services.PoliticianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "app.db-init", havingValue= "true")
public class DBInitializer implements CommandLineRunner {
    @Autowired
    private CitizenService cS;
    @Autowired
    private RoleRepository rR;
    @Autowired
    private PoliticianService poS;
    @Autowired
    private ParliamentarianService paS;

    @Override
    public void run(String... args) {
        Faker faker = new Faker(new Locale("pl"));
        createSampleAdmin("admin@zp.pl", "adminadmin","Admin", "Admin",faker.address().city(),faker.address().streetAddress(), "00010100190", "ARY546695");
        createSampleUser("user@zp.pl", "useruser","User", "User",faker.address().city(),faker.address().streetAddress(), "00010133000", "AOS266716");
        createSampleMarszalekSejmu("marszaleksejmu@zp.pl", "marszalekmarszalek","Marszalek", "Sejmu",faker.address().city(),faker.address().streetAddress(), "00010102260", "HNE905783", "AAA111222", "ABC");
        createSampleMarszalekSenatu("marszaleksenatu@zp.pl", "marszalekmarszalek","Marszalek", "Senatu",faker.address().city(),faker.address().streetAddress(), "02032857461", "BWY747731", "BBB111222", "DEF");
        createSamplePosel("posel1@zp.pl", "posel1posel1", "Anna", "Nowak", faker.address().city(),faker.address().streetAddress(), "09122145291", "OHA621550", "AAA111111", "ABC");
        createSamplePosel("posel2@zp.pl", "posel2posel2", "Jan", "Kowalski","Konstancin-Jeziorna","al. Lipski 0761", "08030261521", "SOU105394", "AAA222111", "DEF");
        createSamplePosel("posel3@zp.pl", "posel3posel3", "Bonifacy", "Wójcik",faker.address().city(),faker.address().streetAddress(), "09090474258", "RLD507081", "AAA333111", "FGH");
        createSamplePosel("posel4@zp.pl", "posel4posel4", "Maurycy", "Wiśniewski",faker.address().city(),faker.address().streetAddress(), "08122579844", "TNV813623", "AAA444111", "FGH");
        createSamplePosel("posel5@zp.pl", "posel5posel5", "Alfred", "Szymański",faker.address().city(),faker.address().streetAddress(), "01070493316", "MTE724079", "AAA555111", "ABC");
        createSampleSenator("senator1@zp.pl", "senator1senator1", "Maria", "Woźniak",faker.address().city(),faker.address().streetAddress(), "00021086865", "MWQ431891", "BBB111111", "ABC");
        createSampleSenator("senator2@zp.pl", "senator2senator2", "Luiza", "Zielińki",faker.address().city(),faker.address().streetAddress(), "01052466736", "FSB511566", "BBB222111", "DEF");
        createSampleSenator("senator3@zp.pl", "senator3senator3", "Romuald", "Kowalczyk",faker.address().city(),faker.address().streetAddress(), "08100212961", "NWK819504", "BBB333111", "FGH");
        createSampleSenator("senator4@zp.pl", "senator4senator4", "Alfons", "Szymczyk",faker.address().city(),faker.address().streetAddress(), "00111523793", "XFO160117", "BBB444111", "FGH");
        createSampleSenator("senator5@zp.pl", "senator5senator5", "Rajmund", "Markowski",faker.address().city(),faker.address().streetAddress(), "07040132841", "NSH117627", "BBB555111", "ABC");
        createSamplePresident("prezydent@zp.pl", "prezydentprezydent","Pan", "Prezydent",faker.address().city(),faker.address().streetAddress(), "78012957218", "RYB838889");

    }

    private void createSamplePresident(String email, String password, String name, String surname,  String town, String address, String pesel, String idnumber){
        CitizenEntity president=  createBasicUser(email, password, name, surname,town,address,pesel, idnumber);
        String[] roles =  {"ROLE_PREZYDENT"};
        setRole(president,roles);
        cS.create(president);
    }

    private void createSampleAdmin(String email, String password, String name, String surname,  String town, String address, String pesel, String idnumber){
        CitizenEntity admin=  createBasicUser(email, password, name, surname,town,address,pesel, idnumber);
        String[] roles =  {"ROLE_ADMIN"};
        setRole(admin,roles);
        cS.create(admin);
    }

    private void createSampleUser(String email, String password, String name, String surname,  String town, String address, String pesel, String idnumber){
        CitizenEntity user=  createBasicUser(email, password, name, surname,town,address,pesel, idnumber);
        String[] roles =  {"ROLE_USER"};
        setRole(user, roles);
        cS.create(user);
    }

    private void createSampleMarszalekSejmu(String email, String password, String name, String surname,  String town, String address, String pesel, String idnumber, String parlIdCard, String politicalGroup){
        CitizenEntity marszalek=  createBasicUser(email, password, name, surname,town,address,pesel, idnumber);
        String[] roles =  {"ROLE_USER", "ROLE_MARSZALEK_SEJMU", "ROLE_POSEL"};
        setRole(marszalek, roles);
        cS.create(marszalek);
        PoliticianEntity politician= new PoliticianEntity(marszalek);
        poS.create(politician);
        ParliamentarianEntity parliamentarian= new ParliamentarianEntity(parlIdCard, politicalGroup, "Sejm", politician);
        paS.create(parliamentarian);
    }

    private void createSampleMarszalekSenatu(String email, String password, String name, String surname,  String town, String address, String pesel, String idnumber, String parlIdCard, String politicalGroup){
        CitizenEntity marszalek=  createBasicUser(email, password, name, surname,town,address,pesel, idnumber);
        String[] roles =  {"ROLE_USER", "ROLE_MARSZALEK_SENATU", "ROLE_SENATOR"};
        setRole(marszalek, roles);
        cS.create(marszalek);
        PoliticianEntity politician= new PoliticianEntity(marszalek);
        poS.create(politician);
        ParliamentarianEntity parliamentarian= new ParliamentarianEntity(parlIdCard, politicalGroup, "Senat", politician);
        paS.create(parliamentarian);
    }

    private void createSampleSenator(String email, String password, String name, String surname,  String town, String address, String pesel, String idnumber, String parlIdCard, String politicalGroup){
        CitizenEntity senator=  createBasicUser(email, password, name, surname, town, address, pesel, idnumber);
        String[] roles =  {"ROLE_USER", "ROLE_SENATOR"};
        setRole(senator, roles);
        cS.create(senator);
        PoliticianEntity politician= new PoliticianEntity(senator);
        poS.create(politician);
        ParliamentarianEntity parliamentarian= new ParliamentarianEntity(parlIdCard, politicalGroup, "Senat", politician);
        paS.create(parliamentarian);
    }

    private void createSamplePosel(String email, String password, String name, String surname, String town, String address, String pesel, String idnumber, String parlIdCard, String politicalGroup){
        CitizenEntity politician=  createBasicUser(email, password, name, surname, town, address, pesel, idnumber);
        String[] roles =  {"ROLE_USER", "ROLE_POSEL"};
        setRole(politician, roles);
        cS.create(politician);
        PoliticianEntity p= new PoliticianEntity(politician);
        poS.create(p);
        ParliamentarianEntity parliamentarian= new ParliamentarianEntity(parlIdCard, politicalGroup, "Sejm", p);
        paS.create(parliamentarian);
    }

    private void setRole(CitizenEntity citizen, String[] roles){
        List<Role> r = new ArrayList<>();
        for ( String role : roles ) {
            Optional<Role> temp = rR.findByName(role);
            temp.ifPresent(r::add);
        }
        citizen.setRoles(r);
    }

    private CitizenEntity createBasicUser(String email, String password, String name, String surname, String town, String address, String pesel, String idnumber){
        CitizenEntity user=  new CitizenEntity(password,password, email, name, surname, town, address, pesel, idnumber);
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        return user;
    }

}
