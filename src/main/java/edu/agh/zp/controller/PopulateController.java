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
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import static java.lang.StrictMath.abs;

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

    @Autowired
    OptionSetRepository OptionSetSession;

    @Autowired
    OptionRepository OptionSession;

    @Autowired
    SetRepository SetSession;

    @GetMapping("/populate_basic")
    public String basicCreate() {
        DocumentTypeSession.saveAll(Arrays.asList(
                new DocumentTypeEntity("Ustawa")
                , new DocumentTypeEntity("Postanowienie")
                , new DocumentTypeEntity("Obwieszczenie")
                , new DocumentTypeEntity("Uchwała")
                , new DocumentTypeEntity("Oświadczenie")
                , new DocumentTypeEntity("Wyrok Trybunały Konstytucyjnego")
                , new DocumentTypeEntity("Umowa międzynarodowa")
                , new DocumentTypeEntity("Rezolucja")
                , new DocumentTypeEntity("Protokół")
                , new DocumentTypeEntity("Rozporządzenie")));

        DocumentStatusRepository.saveAll(Arrays.asList(
                new DocumentStatusEntity("Zgłoszona")
                , new DocumentStatusEntity("Odrzucona")
                , new DocumentStatusEntity("Wygasła")
                , new DocumentStatusEntity("Uchwalona")
                , new DocumentStatusEntity("Obowiązująca")
                , new DocumentStatusEntity("Oczekująca - prezydent")
                , new DocumentStatusEntity("Oczekująca - skierowana do Trybunału ")
                , new DocumentStatusEntity("Oczekująca - sejm")
                , new DocumentStatusEntity("Oczekująca - sejm, powtórnie")
                , new DocumentStatusEntity("Oczekująca - senat")));

        FunctionSession.saveAll(Arrays.asList(
                new FunctionEntity("poseł"),
                new FunctionEntity("senator"),
                new FunctionEntity("prezydent"),
                new FunctionEntity("marszałek"),
                new FunctionEntity("premier"),
                new FunctionEntity("minister"),
                new FunctionEntity("członek komisji")));

        ArrayList<OptionEntity> OptionList = new ArrayList<OptionEntity>(Arrays.asList(new OptionEntity("Za"), new OptionEntity("Przeciw"), new OptionEntity("Wstrzymał się")));
        SetEntity set = new SetEntity("głosowanie parlamentarne");
        OptionSession.saveAll(OptionList);
        SetSession.save(set);
        for (int i = 0; i<3;i++)
        {
            OptionSetSession.save(new OptionSetEntity(OptionList.get(i),set));
        }
        OptionList = new ArrayList<OptionEntity>(Arrays.asList(new OptionEntity("TAK"), new OptionEntity("NIE")));
        set = new SetEntity("referendum");
        OptionSession.saveAll(OptionList);
        SetSession.save(set);
        for (int i = 0; i<2;i++)
        {
            OptionSetSession.save(new OptionSetEntity(OptionList.get(i),set));
        }
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
        for (int i = 0; i < num; i++) {
            String name = faker.name().firstName();
            String lastName = faker.name().lastName();
            //String pass = RandomString.make(15);
            String pass = BCrypt.hashpw("12345678", BCrypt.gensalt()); //Fajnie znać hasłą do tych kont, i fajnie je zahaszować
            CitizenEntity citizen = new CitizenEntity(
                    pass,
                    pass,
                    name + "." + lastName + (rand.nextInt(100) + 1) + "@example.com",
                    name,
                    lastName,
                    faker.address().city(),
                    faker.address().streetAddress(),
                    GeneratePesel(),
                    genID());
            CitizenList.add(citizen);
            if (i % 5 < 3) {
                PoliticianEntity politician = new PoliticianEntity(citizen);
                PoliticianList.add(politician);
                if (i % 5 < 2) {
                    String fun;
                    if (rand.nextInt(2) == 0) fun = "Senat";
                    else fun = "Sejm";
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

    private String genID() {
        Random rand = new Random();
        String res1 = "", res2 = "";
        for (int i = 0; i < 3; i++) {
            res1 += (char)(rand.nextInt(26) + 65);
        }
        res2 = Integer.toString(rand.nextInt(100000));
        while (res2.length() < 5) res2 = "0" + res2;
        int last = CalculateControl_ID(res1,res2);
        return res1 + last + res2;
    }

    private int GetValue(char c) {
        return (int) c - 55;
    }

    private int CalculateControl_ID(String alpha, String digit)
    {
        int control = 7*GetValue(alpha.charAt(0))+3*GetValue(alpha.charAt(1))+GetValue(alpha.charAt(2))
                + 7 * (digit.charAt(0)-48)+ 3 * (digit.charAt(1)-48)+  (digit.charAt(2)-48)
                + 7 * (digit.charAt(3)-48)+ 3 * (digit.charAt(4)-48);

        return control%10;
    }

    private String GeneratePesel()
    {
        Random rand = new Random();
        String result = Long.toString(abs(rand.nextLong()/100000000000L));
        while (result.length() < 10) result = "0" + result;
        result+=CalculateControl_ID(result);
        return result;
    }

    private char CalculateControl_ID(String val)
    {
        int temp = 9*(val.charAt(0)-48) + 7*(val.charAt(1)-48) + 3*(val.charAt(2)-48) +
                (val.charAt(3)-48) + 9*(val.charAt(4)-48) + 7*(val.charAt(5)-48) +
                3*(val.charAt(6)-48) + (val.charAt(7)-48) + 9*(val.charAt(8)-48) +
                7*(val.charAt(9)-48);
        String ret = Integer.toString(temp);
        return ret.charAt(ret.length()-1);
    }

}


