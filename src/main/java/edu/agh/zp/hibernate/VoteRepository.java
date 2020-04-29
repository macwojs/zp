package edu.agh.zp.hibernate;

import edu.agh.zp.objects.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<VoteEntity, Long> {
}
