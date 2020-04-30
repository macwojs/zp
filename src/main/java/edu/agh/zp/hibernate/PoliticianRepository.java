package edu.agh.zp.hibernate;

import edu.agh.zp.objects.PoliticianEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoliticianRepository extends JpaRepository<PoliticianEntity, Long> {
}
