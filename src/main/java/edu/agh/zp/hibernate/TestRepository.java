package edu.agh.zp.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TestRepository extends JpaRepository<TestEntity, Long> {
//    List<TestEntity> findByName(String Name);
//    List<TestEntity> findAll();
}