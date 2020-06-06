package edu.agh.zp.repositories;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.OptionEntity;
import edu.agh.zp.objects.VoteEntity;
import edu.agh.zp.objects.VotingEntity;
import org.apache.catalina.startup.ListenerCreateRule;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository< VoteEntity, Long > {
	Optional< VoteEntity > findByCitizenID_CitizenIDAndVotingID_VotingID(long voting, long citizen );

	List<VoteEntity> findAllByVotingID(VotingEntity voting);
	List<VoteEntity> findAllByVotingIDAndOptionID_OptionDescription(VotingEntity voting, String OptionDescription);
	List<VoteEntity> findAllByVotingIDAndOptionID(VotingEntity voting, OptionEntity option);

	List<VoteEntity> findAllByVotingID_VotingID(long voting);
	List<VoteEntity> findAllByVotingID_VotingIDAndOptionID_OptionDescription(long voting, String OptionDescription);
	List<VoteEntity> findAllByVotingID_VotingIDAndOptionID_OptionID(long voting, long option);

	long countAllByVotingIDAndOptionID(VotingEntity voting, OptionEntity option);
	long countAllByVotingIDAndOptionID_OptionDescription(VotingEntity voting, String optionDescription);
	long countAllByVotingID(VotingEntity voting);

	long countAllByVotingID_VotingIDAndOptionID_OptionID(long voting, long option);
	long countAllByVotingID_VotingIDAndOptionID_OptionDescription(long voting, String optionDescription);
	long countAllByVotingID_VotingID(long voting);

	@Query ( value = "SELECT * FROM vote u WHERE (u.votingid = ?1 and u.citizenid = ?2)", nativeQuery = true )
	Optional< VoteEntity > findByCitizenIdVotingId( long voting, long citizen );

	@Query ( value = "SELECT * FROM vote u WHERE u.votingid = ?1", nativeQuery = true )
	List<VoteEntity> findAllByVotingId(long voting);
}
