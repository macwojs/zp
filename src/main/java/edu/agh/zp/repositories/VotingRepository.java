package edu.agh.zp.repositories;

import edu.agh.zp.objects.DocumentEntity;
import edu.agh.zp.objects.DocumentStatusEntity;
import edu.agh.zp.objects.DocumentTypeEntity;
import edu.agh.zp.objects.VotingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.Collection;
import java.sql.Time;
import java.util.List;

@Repository
public interface VotingRepository extends JpaRepository<VotingEntity, Long> {
    VotingEntity findByVotingID(Long VotingID);

    List<VotingEntity> findByVotingDate(Date votingDate);
    List<VotingEntity> findByVotingDateBetweenAndVotingTypeIsInOrderByVotingDateAscOpenVotingAsc(Date votingDate, Date votingDate2, Collection<VotingEntity.TypeOfVoting> votingType);
    List<VotingEntity> findByVotingDateBetweenAndVotingTypeOrderByVotingDateAscOpenVotingAsc(Date votingDate, Date votingDate2, VotingEntity.TypeOfVoting votingType);
    List<VotingEntity> findByVotingDateBeforeAndVotingTypeOrderByVotingDateDescOpenVotingDesc(Date votingDate, VotingEntity.TypeOfVoting votingType);
    List<VotingEntity> findByVotingDateAndCloseVotingBeforeAndVotingTypeOrderByVotingDateDescOpenVotingDesc(Date votingDate, Time closeTime, VotingEntity.TypeOfVoting votingType);
    List<VotingEntity> findByVotingDateAfterAndVotingTypeIsInOrderByVotingDateAscOpenVotingAsc(Date votingDate, Collection<VotingEntity.TypeOfVoting> votingType);

    Page< VotingEntity > findAllByVotingTypeAndVotingDateAfter( VotingEntity.TypeOfVoting votingType, Date date, Pageable page);

    List<VotingEntity> findByVotingDateAndDocumentIDDocStatusIDDocStatusIDIn(Date date, List< Long > status);
    List<VotingEntity> findByVotingDateAndDocumentIDDocStatusIDDocStatusIDInOrderByOpenVoting(Date date, List< Long > status);
}
