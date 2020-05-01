package edu.agh.zp.repositories;

import edu.agh.zp.objects.PoliticianEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoliticianRepository extends JpaRepository<PoliticianEntity, Long> {
}
