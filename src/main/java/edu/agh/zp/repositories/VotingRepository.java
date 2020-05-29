package edu.agh.zp.repositories;


import edu.agh.zp.objects.DocumentEntity;
import edu.agh.zp.objects.DocumentStatusEntity;
import edu.agh.zp.objects.VotingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.Collection;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

public interface VotingRepository extends JpaRepository<VotingEntity, Long> {
    VotingEntity findByVotingID(Long VotingID);

    List<VotingEntity> findByVotingDate(Date votingDate);
    List<VotingEntity> findByVotingDateBetweenAndVotingTypeIsInOrderByVotingDateAscOpenVotingAsc(Date votingDate, Date votingDate2, Collection<VotingEntity.TypeOfVoting> votingType);
    List<VotingEntity> findByVotingDateBetweenAndVotingTypeOrderByVotingDateAscOpenVotingAsc(Date votingDate, Date votingDate2, VotingEntity.TypeOfVoting votingType);
    List<VotingEntity> findByVotingDateBeforeAndVotingTypeOrderByVotingDateDescOpenVotingDesc(Date votingDate, VotingEntity.TypeOfVoting votingType);
    List<VotingEntity> findByVotingDateAndCloseVotingBeforeAndVotingTypeOrderByVotingDateDescOpenVotingDesc(Date votingDate, Time closeTime, VotingEntity.TypeOfVoting votingType);

    @Query (value="Select * from voting WHERE votingdate=?1 and (docstatusid=1 or docstatusid=5 or docstatusid=6 or docstatusid=8 or docstatusid=9)", nativeQuery=true)
    List< VotingEntity > findByDateForSejm( Date date );

    @Query (value="Select * from voting WHERE votingdate=?1 and docstatusid=2", nativeQuery=true)
    List< VotingEntity > findByDateForSenat( Date date );

    List<VotingEntity> findByVotingDateAndDocumentIDDocStatusIDDocStatusIDIn(Date date, List< Long > status);
}
