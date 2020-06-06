package edu.agh.zp.repositories;

import edu.agh.zp.objects.CommitteeMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitteeMemberRepository extends JpaRepository<CommitteeMemberEntity, Long> {
}
