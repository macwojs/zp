package edu.agh.zp.repositories;

import edu.agh.zp.objects.PositionEntity;
import edu.agh.zp.objects.VotingTimerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingTimerRepository extends JpaRepository<VotingTimerEntity, Long> {
}
