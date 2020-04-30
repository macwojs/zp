package edu.agh.zp.hibernate;

import edu.agh.zp.objects.VotingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingRepository extends JpaRepository<VotingEntity, Long> {
}
