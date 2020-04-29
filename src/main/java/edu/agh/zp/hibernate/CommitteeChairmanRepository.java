package edu.agh.zp.hibernate;

import edu.agh.zp.objects.CommitteeChairmanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitteeChairmanRepository extends JpaRepository<CommitteeChairmanEntity, Long> {
}
