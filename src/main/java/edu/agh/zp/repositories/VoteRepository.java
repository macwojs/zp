package edu.agh.zp.repositories;

import edu.agh.zp.objects.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<VoteEntity, Long> {
}
