package edu.agh.zp.repositories;

import edu.agh.zp.objects.ParliamentarianEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParliamentarianRepository extends JpaRepository<ParliamentarianEntity, Long> {
}
