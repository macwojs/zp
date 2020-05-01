package edu.agh.zp.repositories;

import edu.agh.zp.objects.VotingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingRepository extends JpaRepository<VotingEntity, Long> {
}
