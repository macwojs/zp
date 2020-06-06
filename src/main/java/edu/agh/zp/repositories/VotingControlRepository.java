package edu.agh.zp.repositories;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.VotingControlEntity;
import edu.agh.zp.objects.VotingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface VotingControlRepository extends JpaRepository<VotingControlEntity, Long> {
     @Transactional
     void deleteByVotingID(VotingEntity votingID);
     Optional<VotingControlEntity> findByCitizenIDAndVotingID(CitizenEntity citizenID, VotingEntity votingID);
}