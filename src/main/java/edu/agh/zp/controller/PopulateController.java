// Controller should be used for creating basic constant records in data base, just after initialization/flush,
package edu.agh.zp.controller;

import edu.agh.zp.hibernate.*;
import edu.agh.zp.objects.DocumentStatusEntity;
import edu.agh.zp.objects.DocumentTypeEntity;
import edu.agh.zp.objects.FunctionEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
public class PopulateController {

    @Autowired
    private DocumentTypeRepository DocumentTypeSession;

    @Autowired
    private DocumentStatusRepository DocumentStatusRepository;

    @Autowired
    private FunctionRepository FunctionSession;



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


    @GetMapping("/truncate_basic")
    public String truncate(){
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

