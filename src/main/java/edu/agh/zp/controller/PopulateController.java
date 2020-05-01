// Controller should be used for creating basic constant records in data base, just after initialization/flush,
package edu.agh.zp.controller;


import com.github.javafaker.Faker;
import edu.agh.zp.repositories.*;
import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.DocumentStatusEntity;
import edu.agh.zp.objects.DocumentTypeEntity;
import edu.agh.zp.objects.FunctionEntity;
import edu.agh.zp.objects.*;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

@RestController
public class PopulateController {

    @Autowired
    private DocumentTypeRepository DocumentTypeSession;

    @Autowired
    private DocumentStatusRepository DocumentStatusRepository;

    @Autowired
    private FunctionRepository FunctionSession;

    @Autowired
    private CitizenRepository CitizenSession;

    @Autowired
    private PoliticianRepository PoliticianSession;

    @Autowired
    private ParliamentarianRepository ParliamentarianSession;


    @GetMapping("/populate_basic")

    public String basicCreate() {
        DocumentTypeSession.saveAll(Arrays.asList(new DocumentTypeEntity("Ustawa")
                , new DocumentTypeEntity("Rozporządzenie")
                , new DocumentTypeEntity("coś_innego")
                , new DocumentTypeEntity("wymyśl_coś")));

        DocumentStatusRepository.saveAll(Arrays.asList(
                new DocumentStatusEntity("Odrzucona"),
                new DocumentStatusEntity("Aktywna"),
                new DocumentStatusEntity("Wygasła"),
                new DocumentStatusEntity("Oczekująca - prezydent"),
                new DocumentStatusEntity("Oczekująca - sejm"),
                new DocumentStatusEntity("Oczekująca - senat")));

        FunctionSession.saveAll(Arrays.asList(
                new FunctionEntity("poseł"),
                new FunctionEntity("senator"),
                new FunctionEntity("prezydent"),
                new FunctionEntity("marszałek"),
                new FunctionEntity("premier"),
                new FunctionEntity("minister"),
                new FunctionEntity("członek komisji")));

        return "Customers are created";
    }

    @GetMapping("/populate/{num}")
    public String Create(@PathVariable int num) {
        if (DocumentTypeSession.count() == 0 || DocumentStatusRepository.count() == 0 || FunctionSession.count() == 0) {
            truncate();
            basicCreate();
        }
        ArrayList<String> group = new ArrayList<String>(Arrays.asList("AAA", "BBB", "CCC", "DDD"));
        Faker faker = new Faker(new Locale("pl"));
        ArrayList<CitizenEntity> CitizenList = new ArrayList<CitizenEntity>();
        ArrayList<PoliticianEntity> PoliticianList = new ArrayList<PoliticianEntity>();
        ArrayList<ParliamentarianEntity> ParliamentarianList = new ArrayList<ParliamentarianEntity>();
        Random rand = new Random();
        long Pesel = 11111111111L;
        for (int i = 0; i < num; i++) {
            String name = faker.name().firstName();
            String lastName = faker.name().lastName();
            String pass = RandomString.make(15);
            CitizenEntity citizen = new CitizenEntity(
                    pass,
                    pass,
                    name + "." + lastName + (rand.nextInt(100) + 1) + "@example.com",
                    name,
                    lastName,
                    Long.toString(Pesel),
                    genID());
            CitizenList.add(citizen);
            Pesel++;
            if (i % 5 < 3) {
                PoliticianEntity politician = new PoliticianEntity(citizen);
                PoliticianList.add(politician);
                if (i % 5 < 2) {
                    String fun;
                    if (rand.nextInt(2) == 0) fun = "Senator";
                    else fun = "Poseł";
                    ParliamentarianList.add(new ParliamentarianEntity(
                            RandomString.make(10),
                            group.get(rand.nextInt(group.size())),
                            fun,
                            politician
                    ));
                }
                //function adding
            }
        }
        CitizenSession.saveAll(CitizenList);
        PoliticianSession.saveAll(PoliticianList);
        ParliamentarianSession.saveAll(ParliamentarianList);
        return "Populated " + num;
    }

    @GetMapping("/truncate_basic")
    public String truncate() {
        DocumentTypeSession.deleteAll();
        DocumentStatusRepository.deleteAll();
        FunctionSession.deleteAll();
        return "Truncated";
    }

    @GetMapping("/truncate_real")
    public String truncate_force() {
        DocumentTypeSession.deleteAll();
        DocumentStatusRepository.deleteAll();
        FunctionSession.deleteAll();
        return "Truncated";
    }

    public String genID() {
        Random rand = new Random();
        String res1 = "", res2 = "";
        for (int i = 0; i < 3; i++) {
            res1 += (char)(rand.nextInt(26) + 65);
        }
        res2 = Integer.toString(rand.nextInt(100000));
        while (res2.length() < 5) res2 = "0" + res2;
        int last = CalculateControl(res1,res2);
        return res1 + last + res2;
    }

    public int GetValue(char c) {
        return (int) c - 55;
    }

    public int CalculateControl(String alpha, String digit)
    {
        int control = 7*GetValue(alpha.charAt(0))+3*GetValue(alpha.charAt(1))+GetValue(alpha.charAt(2))
                + 7 * (digit.charAt(0)-48)+ 3 * (digit.charAt(1)-48)+  (digit.charAt(2)-48)
                + 7 * (digit.charAt(3)-48)+ 3 * (digit.charAt(4)-48);

        return control%10;
    }
}


