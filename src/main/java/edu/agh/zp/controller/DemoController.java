package edu.agh.zp.controller;

import edu.agh.zp.objects.TestEntity;
import edu.agh.zp.repositories.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController

public class DemoController {


    @Autowired
    private TestRepository testRepository;

    @GetMapping("/bulkcreate")

    public String bulkcreate(){



        testRepository.save(new TestEntity("qwerty", 11));



        testRepository.saveAll(Arrays.asList(new TestEntity("Salim", 32)

                , new TestEntity("Rajesh", 22)

                , new TestEntity("Rahul", 11)

                , new TestEntity("Dharmendra", 321)));

        return "Customers are created";

    }




    @GetMapping("/findall")

    public List<TestEntity> findAll(){

        List<TestEntity> tests = testRepository.findAll();

        List<TestEntity> test_list = new ArrayList<>();

        for (TestEntity test : tests) {

            test_list.add(new TestEntity(test.getName(),test.getNum()));

        }

        return test_list;

    }

    @RequestMapping("/search/{id}")

    public String search(@PathVariable long id){

        String Test = "";

        Test = testRepository.findById(id).toString();

        return Test;

    }



}