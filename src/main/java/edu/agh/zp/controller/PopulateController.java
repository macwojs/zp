// Controller should be used for creating basic constant records in data base, just after initialization/flush,
package edu.agh.zp.controller;

import com.github.javafaker.Faker;
import edu.agh.zp.hibernate.*;
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

    public String basicCreate(){
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
    public String Create(@PathVariable int num ) {
        if (DocumentTypeSession.count() ==0 || DocumentStatusRepository.count() ==0 || FunctionSession.count() == 0)
        {
            truncate();
            basicCreate();
        }
        ArrayList<String> group = new ArrayList<String>(Arrays.asList("AAA", "BBB", "CCC", "DDD"));
        Faker faker = new Faker(new Locale("pl"));
//        ArrayList<FunctionEntity> fun = new ArrayList<FunctionEntity>(Arrays.asList(
//                FunctionSession.findByFunName("poseł").get(0),
//                FunctionSession.findByFunName("senator").get(0)));
        ArrayList<CitizenEntity> CitizenList = new ArrayList<CitizenEntity>();
        ArrayList<PoliticianEntity> PoliticianList = new ArrayList<PoliticianEntity>();
        ArrayList<ParliamentarianEntity> ParliamentarianList = new ArrayList<ParliamentarianEntity>();
        Random rand = new Random();
        long Pesel = 11111111111L;
        for (int i=0; i<num;i++)
        {
            String name = faker.name().firstName();
            String lastName = faker.name().lastName();
            CitizenEntity citizen = new CitizenEntity(
                    RandomString.make(15),
                    name + "."+ lastName + (rand.nextInt(100) + 1) + "@example.com",
                    name,
                    lastName,
                    Long.toString(Pesel));
            CitizenList.add(citizen);
            Pesel++;
            if (i % 5 < 3)
            {
                PoliticianEntity politician = new PoliticianEntity(citizen);
                PoliticianList.add(politician);
                if (i % 5 < 2)
                {
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
    public String truncate(){
        DocumentTypeSession.deleteAll();
        DocumentStatusRepository.deleteAll();
        FunctionSession.deleteAll();
        return "Truncated";
    }

    @GetMapping("/truncate_real")
    public String truncate_force(){
        DocumentTypeSession.deleteAll();
        DocumentStatusRepository.deleteAll();
        FunctionSession.deleteAll();
        return "Truncated";
    }




//    @GetMapping("/findall")
//
//    public List<TestEntity> findAll(){
//
//        List<TestEntity> tests = testRepository.findAll();
//
//        List<TestEntity> test_list = new ArrayList<>();
//
//        for (TestEntity test : tests) {
//
//            test_list.add(new TestEntity(test.getName(),test.getNum()));
//
//        }
//
//        return test_list;
//
//    }
//
//    @RequestMapping("/search/{id}")
//
//    public String search(@PathVariable long id){
//
//        String Test = "";
//
//        Test = testRepository.findById(id).toString();
//
//        return Test;
//
//    }
}

