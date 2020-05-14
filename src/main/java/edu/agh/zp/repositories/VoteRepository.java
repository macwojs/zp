package edu.agh.zp.repositories;

import edu.agh.zp.objects.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VoteRepository extends JpaRepository< VoteEntity, Long > {
	@Query ( value = "SELECT * FROM vote u WHERE (u.votingid = ?1 and u.citizenid = ?2)", nativeQuery = true )
	Optional< VoteEntity > findByCitizenIdVotingId( long voting, long citizen );
}
