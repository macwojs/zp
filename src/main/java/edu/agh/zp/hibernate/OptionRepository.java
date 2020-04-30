package edu.agh.zp.hibernate;

import edu.agh.zp.objects.OptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<OptionEntity, Long> {
}
