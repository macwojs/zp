package edu.agh.zp.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentStatusRepository extends JpaRepository<DocumentStatusEntity, Long> {
    List<DocumentStatusEntity> findByDocStatusName(String Name);
    List<DocumentStatusEntity> findAll();
}