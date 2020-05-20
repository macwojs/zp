package edu.agh.zp.repositories;

import edu.agh.zp.objects.SetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SetRepository extends JpaRepository<SetEntity, Long> {
	Optional< SetEntity > findBySetID( Long primaryKey);
	Optional<SetEntity> findBySetName(String name);
}
