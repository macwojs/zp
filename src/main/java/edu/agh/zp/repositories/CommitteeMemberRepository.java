package edu.agh.zp.repositories;

import edu.agh.zp.objects.CommitteeMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitteeMemberRepository extends JpaRepository<CommitteeMemberEntity, Long> {
}
