package edu.agh.zp.services;

import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class VoteService {
    @Autowired
    VoteRepository vR;

    public VoteEntity create(VoteEntity vote){
        return vR.save(vote);
    }

    public VoteEntity update(VoteEntity vote ){
        return vR.save(vote);
    }

    public long countAllByVoting(VotingEntity voting){
        return vR.countAllByVotingID(voting);
    }

    public long countByVotingAndOption(VotingEntity voting, OptionEntity option){
        return vR.countAllByVotingIDAndOptionID(voting,option);
    }

    public long countByVotingAndOptionDescription(VotingEntity voting, String optionDescription){
            return vR.countAllByVotingIDAndOptionID_OptionDescription(voting, optionDescription);
    }

    public long countAllByVotingID(long voting){
        return vR.countAllByVotingID_VotingID(voting);
    }

    public long countByVotingIDAndOptionID(long voting, long option){
        return vR.countAllByVotingID_VotingIDAndOptionID_OptionID(voting,option);
    }

    public long countByVotingIDAndOptionDescription(long voting, String optionDescription){
        return vR.countAllByVotingID_VotingIDAndOptionID_OptionDescription(voting, optionDescription);
    }

    ArrayList<VoteEntity> findAllByVoting(VotingEntity voting){
        return (ArrayList<VoteEntity>)vR.findAllByVotingID(voting);
    }

    ArrayList<VoteEntity> findAllByVotingAndOptionDescription(VotingEntity voting, String optionDescription){
        return (ArrayList<VoteEntity>)vR.findAllByVotingIDAndOptionID_OptionDescription(voting, optionDescription);
    }

    ArrayList<VoteEntity> findByVotingAndOption(VotingEntity voting, OptionEntity option){
        return (ArrayList<VoteEntity>)vR.findAllByVotingIDAndOptionID(voting, option);
    }

    ArrayList<VoteEntity> findAllByVotingID(long voting){
        return (ArrayList<VoteEntity>)vR.findAllByVotingID_VotingID(voting);
    }

    ArrayList<VoteEntity> findAllByVotingIDAndOptionDescription(long voting, String optionDescription){
        return (ArrayList<VoteEntity>)vR.findAllByVotingID_VotingIDAndOptionID_OptionDescription(voting, optionDescription);
    }

    ArrayList<VoteEntity> findByVotingIDAndOptionID(long voting, long option){
        return (ArrayList<VoteEntity>)vR.findAllByVotingID_VotingIDAndOptionID_OptionID(voting, option);
    }

}
