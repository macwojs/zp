package edu.agh.zp.hibernate;

import edu.agh.zp.objects.ParliamentarianEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParliamentarianRepository extends JpaRepository<ParliamentarianEntity, Long> {
}
