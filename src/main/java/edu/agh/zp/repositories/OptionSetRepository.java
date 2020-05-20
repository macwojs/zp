package edu.agh.zp.repositories;
import edu.agh.zp.objects.OptionEntity;
import edu.agh.zp.objects.OptionSetEntity;
import edu.agh.zp.objects.SetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface OptionSetRepository extends JpaRepository<OptionSetEntity, Long> {
    Optional<OptionSetEntity> findByOptionSetID(Long id);
    List<OptionSetEntity> findAllByOptionSetID_SetID(SetEntity id);

}
