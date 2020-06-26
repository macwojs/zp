package edu.agh.zp.repositories;

import edu.agh.zp.objects.OptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface OptionRepository extends JpaRepository<OptionEntity, Long> {
    Optional< OptionEntity > findByOptionID(Long id);
    List<OptionEntity> findAllByOptionIDIsIn(ArrayList<Long> options);
}
