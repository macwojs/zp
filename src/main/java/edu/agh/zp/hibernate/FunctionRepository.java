package edu.agh.zp.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FunctionRepository extends JpaRepository<FunctionEntity, Long> {
    List<FunctionEntity> findByFunName(String Name);
    List<FunctionEntity> findAll();
}
