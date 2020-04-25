package edu.agh.zp.hibernate;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;




public class Demo{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext applicationContext;


    TestRepository repository;

    public static void main(String args) {
        SpringApplication.run(Demo.class, args);
    }


    public void run() throws Exception
    {
        this.repository = applicationContext.getBean(TestRepository.class);
        Optional<TestEntity> test = repository.findById(2L);

        logger.info("Employee id 2 -> {}", test.get());
    }
}