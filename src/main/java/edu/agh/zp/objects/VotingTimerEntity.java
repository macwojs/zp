package edu.agh.zp.objects;


import edu.agh.zp.validator.TimeAfterNow;
import edu.agh.zp.validator.TimeOrder;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Entity(name = "VotingTimer")
@TimeOrder
@TimeAfterNow
public class VotingTimerEntity implements Serializable {

    @Id
    @NotNull
    private long votingID;


    @NotNull
    @Column(name = "eraseTime")
    private Date erase;

    public VotingTimerEntity() {
    }

    public VotingTimerEntity(@NotNull long votingID, @NotNull Date erase) {
        this.votingID = votingID;
        this.erase = erase;
    }

    public long getVotingID() {
        return votingID;
    }

    public void setVotingID(long votingID) {
        this.votingID = votingID;
    }

    public Date getErase() {
        return erase;
    }

    public void setErase(Date erase) {
        this.erase = erase;
    }
}