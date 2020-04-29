package edu.agh.zp.hibernate;

import edu.agh.zp.objects.CommitteeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitteeRepository extends JpaRepository<CommitteeEntity, Long> {
}
