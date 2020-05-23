package edu.agh.zp.objects;





import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;


@Entity(name = "VotingTimer")
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