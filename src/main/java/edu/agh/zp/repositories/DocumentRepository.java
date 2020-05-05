package edu.agh.zp.repositories;

import edu.agh.zp.objects.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
	@Query ("Select * from document WHERE (doctypeid=1 or doctypeid=4 or doctypeid=7) and (docstatusid=1 or docstatusid=8 or docstatusid=9)")
	List<DocumentEntity> findByDocForParlVoting( );
}
