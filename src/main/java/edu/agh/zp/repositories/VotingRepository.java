package edu.agh.zp.repositories;


import edu.agh.zp.objects.VotingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;


public interface VotingRepository extends JpaRepository<VotingEntity, Long> {
    VotingEntity findByVotingID(Long VotingID);

    List<VotingEntity> findByVotingDate(Date votingDate);
    List<VotingEntity> findByVotingDateBetweenAndVotingTypeOrderByVotingDateAscOpenVotingAsc(Date votingDate, Date votingDate2, VotingEntity.TypeOfVoting votingType);
    List<VotingEntity> findByVotingDateBetweenAndVotingTypeOrVotingTypeOrderByVotingDateAscOpenVotingAsc(Date votingDate, Date votingDate2, VotingEntity.TypeOfVoting votingType, VotingEntity.TypeOfVoting votingType2);
}
