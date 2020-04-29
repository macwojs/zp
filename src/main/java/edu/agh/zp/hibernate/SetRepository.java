package edu.agh.zp.hibernate;

import edu.agh.zp.objects.SetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetRepository extends JpaRepository<SetEntity, Long> {
}
