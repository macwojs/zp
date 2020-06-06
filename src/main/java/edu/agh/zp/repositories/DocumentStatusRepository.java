package edu.agh.zp.repositories;

import edu.agh.zp.objects.DocumentStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface DocumentStatusRepository extends JpaRepository<DocumentStatusEntity, Long> {
    List<DocumentStatusEntity> findByDocStatusName(String Name);
    List<DocumentStatusEntity> findAll();

    List<DocumentStatusEntity> findByDocStatusNameIn(Collection<String> Names);
    List<DocumentStatusEntity> findByDocStatusIDIn(List<Long> ids);
    DocumentStatusEntity findByDocStatusID(Long id);
}
