package edu.agh.zp.repositories;

import edu.agh.zp.objects.VotingTimerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.List;

@Repository
public interface VotingTimerRepository extends JpaRepository<VotingTimerEntity, Long> {
    List<VotingTimerEntity> findByEraseBefore(@NotNull Date erase);
 }
