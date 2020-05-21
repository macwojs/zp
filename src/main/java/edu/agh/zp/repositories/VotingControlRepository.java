package edu.agh.zp.repositories;



import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.VotingControlEntity;
import edu.agh.zp.objects.VotingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VotingControlRepository extends JpaRepository<VotingControlEntity, Long> {
     void deleteAllByVotingID(VotingEntity votingID);
     Optional<VotingControlEntity> findByCitizenIDAndVotingID(CitizenEntity citizenID, VotingEntity votingID);
}