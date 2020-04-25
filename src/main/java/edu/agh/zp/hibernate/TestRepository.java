package edu.agh.zp.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



//@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
//    List<TestEntity> findByName(String Name);
//    List<TestEntity> findAll();
}