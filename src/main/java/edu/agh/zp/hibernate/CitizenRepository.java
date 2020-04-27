package edu.agh.zp.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CitizenRepository extends JpaRepository<CitizenEntity, Long> {
}
