package edu.agh.zp.repositories;

import edu.agh.zp.objects.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TestRepository extends JpaRepository<TestEntity, Long> {
//    List<TestEntity> findByName(String Name);
//    List<TestEntity> findAll();
}