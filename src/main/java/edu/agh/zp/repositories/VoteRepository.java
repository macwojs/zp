package edu.agh.zp.repositories;

import edu.agh.zp.objects.VoteEntity;
import org.apache.catalina.startup.ListenerCreateRule;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository< VoteEntity, Long > {
	@Query ( value = "SELECT * FROM vote u WHERE (u.votingid = ?1 and u.citizenid = ?2)", nativeQuery = true )
	Optional< VoteEntity > findByCitizenIdVotingId( long voting, long citizen );

	@Query ( value = "SELECT * FROM vote u WHERE u.votingid = ?1", nativeQuery = true )
	List<VoteEntity> findAllByVotingId(long voting);
}
