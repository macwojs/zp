package edu.agh.zp.repositories;

import edu.agh.zp.objects.DocumentEntity;
import edu.agh.zp.objects.DocumentStatusEntity;
import edu.agh.zp.objects.DocumentTypeEntity;
import org.dom4j.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
	@Query (value="Select * from document WHERE (doctypeid=1 or doctypeid=4 or doctypeid=5 or doctypeid=6 or doctypeid=7) and (docstatusid=1 or docstatusid=5 or docstatusid=6 or docstatusid=8 or docstatusid=9)", nativeQuery=true)
	List<DocumentEntity> findByDocForSejm( );

	@Query (value="Select * from document WHERE (doctypeid=1 or doctypeid=4 or doctypeid=5 or doctypeid=6 or doctypeid=7) and (docstatusid=2)", nativeQuery=true)
	List<DocumentEntity> findByDocForSenat( );


	Optional<DocumentEntity> findByDocID(long docID);

	@Transactional
	@Modifying
	@Query(value="UPDATE document SET docstatusid=?2, lastedit=CURRENT_DATE where docid =?1", nativeQuery=true)
	void UpdateStatusByID(long id , long StatusId);

	@Transactional
	@Modifying
	@Query(value="UPDATE document SET docstatusid=?2, lastedit=CURRENT_DATE, validateddate=CURRENT_DATE where docid =?1", nativeQuery=true)
	void ActivateStatusByID(long id , long StatusId);

	Page<DocumentEntity> findAllByDocStatusID_DocStatusIDIn(Collection<Long> docStatus, Pageable page);

	Page<DocumentEntity> findAllByDocStatusID_DocStatusID(long docStatus, Pageable page);

	Page<DocumentEntity> findAllByDocTypeID_DocTypeID(long docType, Pageable page);

	Page<DocumentEntity> findAllByDocStatusIDInAndDocTypeIDIn(Collection<DocumentStatusEntity> docStatuses, Collection<DocumentTypeEntity> docTypes, Pageable page);

	@Query( value = "SELECT * FROM document WHERE docstatusid IN ?1 AND doctypeid IN ?2 AND ( lastedit >= ?3 OR (  lastedit < ?3 AND (lastedit >= ?3 OR lastedit IS NULL)))", nativeQuery = true)
	Page<DocumentEntity> findAllByStatusAndTypeLastEditAfter( Collection<DocumentStatusEntity> docStatuses, Collection<DocumentTypeEntity> docTypes, Date date, Pageable page);

	@Query( value = "SELECT * FROM document WHERE docstatusid IN ?1 AND doctypeid IN ?2 AND lastedit < ?3", nativeQuery = true)
	Page<DocumentEntity> findAllByStatusAndTypeLastEditBefore( Collection<DocumentStatusEntity> docStatuses, Collection<DocumentTypeEntity> docTypes, Date date, Pageable page);


	@Query( value = "SELECT * FROM document WHERE docstatusid IN ?1 AND doctypeid IN ?2 AND ( lastedit >= ?3 OR (  lastedit < ?3 AND (lastedit >= ?3 OR lastedit IS NULL)))", nativeQuery = true)
	Page<DocumentEntity> findAllByStatusAndTypeAfter( Collection<DocumentStatusEntity> docStatuses, Collection<DocumentTypeEntity> docTypes, Date date, Pageable page);

	@Query( value = "SELECT * FROM document WHERE docstatusid IN ?1 AND doctypeid IN ?2 AND lastedit < ?3", nativeQuery = true)
	Page<DocumentEntity> findAllByStatusAndTypeBefore( Collection<DocumentStatusEntity> docStatuses, Collection<DocumentTypeEntity> docTypes, Date date, Pageable page);

}
