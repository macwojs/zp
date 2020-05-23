package edu.agh.zp.repositories;


import edu.agh.zp.objects.VotingTimerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.List;



public interface VotingTimerRepository extends JpaRepository<VotingTimerEntity, Long> {
    List<VotingTimerEntity> findByEraseBefore(@NotNull Date erase);
 }
