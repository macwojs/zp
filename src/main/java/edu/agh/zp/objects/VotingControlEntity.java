package edu.agh.zp.objects;



import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "VotingControl")
@IdClass(IdVotingControl.class)
public class VotingControlEntity implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "CitizenId")
    private CitizenEntity citizenID;

    @Id
    @ManyToOne
    @JoinColumn(name = "VotingId")
    private VotingEntity votingID;

    public VotingControlEntity() {
    }


    public VotingControlEntity(CitizenEntity citizenID, VotingEntity votingID) {
        this.citizenID = citizenID;
        this.votingID = votingID;
    }

    public CitizenEntity getCitizenID() {
        return citizenID;
    }

    public void setCitizenID(CitizenEntity citizenID) {
        this.citizenID = citizenID;
    }

    public VotingEntity getVotingID() {
        return votingID;
    }

    public void setVotingID(VotingEntity votingID) {
        this.votingID = votingID;
    }
}
