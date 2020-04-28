// Controller should be used for creating basic constant records in data base, just after initialization/flush,
package edu.agh.zp.controller;

import edu.agh.zp.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;


public class PopulateController {

    @Autowired
    private DocumentTypeRepository DocumentTypeSession;

    @Autowired
    private DocumentStatusRepository DocumentStatusRepository;

    @Autowired
    private FunctionRepository FunctionSession;

    @GetMapping("/populate_create")

    public String bulkcreate(){



        DocumentTypeSession.saveAll(Arrays.asList(new DocumentTypeEntity("Salim", 32)

                , new TestEntity("Rajesh", 22)

                , new TestEntity("Rahul", 11)

                , new TestEntity("Dharmendra", 321)));

        return "Customers are created";

    }


    @GetMapping("/truncate")
    public String truncate(){
        session.createSQLQuery("truncate table MyTable").executeUpdate();
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
