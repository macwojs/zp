// Controller should be used for creating basic constant records in data base, just after initialization/flush,
package edu.agh.zp.controller;

import com.github.javafaker.Faker;
import edu.agh.zp.hibernate.*;
import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.DocumentStatusEntity;
import edu.agh.zp.objects.DocumentTypeEntity;
import edu.agh.zp.objects.FunctionEntity;
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

    @Autowired
    private DocumentRepository DocumentSession;

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
        Faker faker = new Faker(new Locale("pl"));
        ArrayList<CitizenEntity> CitizenList = new ArrayList<CitizenEntity>();
        Random rand = new Random();
        long Pesel = 11111111111L;
        for (int i=0; i<num;i++)
        {
            String name = faker.name().firstName();
            String lastName = faker.name().lastName();
            CitizenList.add(new CitizenEntity(
                    RandomString.make(15),
                    name + "."+ lastName + "@example.com",
                    name,
                    lastName,
                    Long.toString(Pesel)));
            Pesel++;
        }
        CitizenSession.saveAll(CitizenList);
        return "Not yet implemented" + num;
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

