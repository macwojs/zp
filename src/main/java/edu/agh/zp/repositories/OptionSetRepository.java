package edu.agh.zp.repositories;

import edu.agh.zp.objects.OptionEntity;
import edu.agh.zp.objects.OptionSetEntity;
import edu.agh.zp.objects.SetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionSetRepository extends JpaRepository<OptionSetEntity, Long> {
    List<OptionSetEntity> findBySetIDcolumn(SetEntity set);
}
