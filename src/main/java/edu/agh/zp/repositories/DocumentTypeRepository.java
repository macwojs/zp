package edu.agh.zp.repositories;

import edu.agh.zp.objects.DocumentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentTypeEntity, Long> {
    List<DocumentTypeEntity> findByDocTypeName(String Name);
    DocumentTypeEntity findByDocTypeID(long Name);


    List<DocumentTypeEntity> findAllByDocTypeIDNotIn(List<Long> asList);
}
