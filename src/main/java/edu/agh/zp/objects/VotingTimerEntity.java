package edu.agh.zp.objects;


import edu.agh.zp.validator.TimeAfterNow;
import edu.agh.zp.validator.TimeOrder;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Time;
import java.util.List;

@Entity(name = "VotingTimer")
@TimeOrder
@TimeAfterNow
public class VotingTimerEntity implements Serializable {

    @Id
    @NotNull
    @Column(name="VotingID")
    private VotingEntity votingID;


    @NotNull
    @Column(name = "eraseTime")
    private Time erase;

    public VotingTimerEntity() {
    }

    public VotingTimerEntity(@NotNull VotingEntity votingID, @NotNull Time erase) {
        this.votingID = votingID;
        this.erase = erase;
    }

    public VotingEntity getVotingID() {
        return votingID;
    }

    public void setVotingID(VotingEntity votingID) {
        this.votingID = votingID;
    }

    public Time getErase() {
        return erase;
    }

    public void setErase(Time erase) {
        this.erase = erase;
    }
}