package edu.agh.zp.repositories;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.OptionEntity;
import edu.agh.zp.objects.VoteEntity;
import edu.agh.zp.objects.VotingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

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

}
