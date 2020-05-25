package edu.agh.zp.repositories;

import edu.agh.zp.objects.OptionEntity;
import edu.agh.zp.objects.OptionSetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface OptionRepository extends JpaRepository<OptionEntity, Long> {
    Optional< OptionEntity > findByOptionID(Long id);
    List<OptionEntity> findAllByOptionIDIsIn(ArrayList<Long> options);
}
