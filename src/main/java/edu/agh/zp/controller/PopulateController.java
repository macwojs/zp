// Controller should be used for creating basic constant records in data base, just after initialization/flush,
package edu.agh.zp.controller;

import edu.agh.zp.hibernate.*;
import edu.agh.zp.objects.DocumentTypeEntity;
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


    @GetMapping("/populate_create")

    public String bulkcreate(){
        DocumentTypeSession.saveAll(Arrays.asList(new DocumentTypeEntity("Act")
                , new DocumentTypeEntity("decree")
                , new DocumentTypeEntity("No_idea_what")
                , new DocumentTypeEntity("Something_else")));

        return "Customers are created";
    }


    @GetMapping("/truncate")
    public String truncate(){
        Configuration cfg = new Configuration();
        cfg.configure("application.properties");
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
        session.beginTransaction();
        session.createSQLQuery("truncate table DocumentType, DocumentStatus, Function").executeUpdate();
        session.getTransaction().commit();
        return "Not yet implemented";
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
