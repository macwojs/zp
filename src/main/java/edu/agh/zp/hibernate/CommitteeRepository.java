package edu.agh.zp.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitteeRepository extends JpaRepository<CommitteeEntity, Long> {
}
