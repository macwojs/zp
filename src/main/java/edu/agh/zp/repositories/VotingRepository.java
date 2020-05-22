package edu.agh.zp.repositories;


import edu.agh.zp.objects.VotingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.Collection;
import java.util.List;


public interface VotingRepository extends JpaRepository<VotingEntity, Long> {
    VotingEntity findByVotingID(Long VotingID);

    List<VotingEntity> findByVotingDate(Date votingDate);
    List<VotingEntity> findByVotingDateBetweenAndVotingTypeIsInOrderByVotingDateAscOpenVotingAsc(Date votingDate, Date votingDate2, Collection<VotingEntity.TypeOfVoting> votingType);
}
