package edu.agh.zp.hibernate;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


public interface TestRepository extends JpaRepository<TestEntity, Long> {
}