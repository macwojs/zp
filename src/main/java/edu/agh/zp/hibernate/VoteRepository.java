package edu.agh.zp.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<VoteEntity, Long> {
}
