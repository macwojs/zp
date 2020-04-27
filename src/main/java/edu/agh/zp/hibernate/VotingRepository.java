package edu.agh.zp.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingRepository extends JpaRepository<VotingEntity, Long> {
}
