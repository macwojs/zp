package edu.agh.zp.repositories;



import edu.agh.zp.objects.VotingControlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingControlRepository extends JpaRepository<VotingControlEntity, Long> {
}