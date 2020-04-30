package edu.agh.zp.hibernate;

import edu.agh.zp.objects.DocumentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentTypeRepository extends JpaRepository<DocumentTypeEntity, Long> {
    List<DocumentTypeEntity> findByDocTypeName(String Name);
    List<DocumentTypeEntity> findAll();
}
