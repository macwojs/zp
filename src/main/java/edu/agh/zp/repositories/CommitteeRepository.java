package edu.agh.zp.repositories;

import edu.agh.zp.objects.CommitteeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitteeRepository extends JpaRepository<CommitteeEntity, Long> {
}
