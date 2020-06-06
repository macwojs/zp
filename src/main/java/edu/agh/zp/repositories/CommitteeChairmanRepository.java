package edu.agh.zp.repositories;

import edu.agh.zp.objects.CommitteeChairmanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitteeChairmanRepository extends JpaRepository<CommitteeChairmanEntity, Long> {
}
