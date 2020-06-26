package edu.agh.zp.repositories;

import edu.agh.zp.objects.DocumentStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

@Repository
public interface DocumentStatusRepository extends JpaRepository<DocumentStatusEntity, Long> {
    List<DocumentStatusEntity> findByDocStatusName(String Name);

    @NotNull
    List<DocumentStatusEntity> findAll();
    List<DocumentStatusEntity> findAllByDocStatusIDIn(Collection<Long> ids);
    List<DocumentStatusEntity> findByDocStatusNameIn(Collection<String> Names);
    DocumentStatusEntity findByDocStatusID(Long id);
}
