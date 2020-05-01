package edu.agh.zp.repositories;

import edu.agh.zp.objects.OptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<OptionEntity, Long> {
}
