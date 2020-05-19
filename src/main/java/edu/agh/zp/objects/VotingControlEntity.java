package edu.agh.zp.objects;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Entity(name = "VotingControl")
@IdClass(IdVotingControl.class)
public class VotingControlEntity implements Serializable {
    @Id
    @Column(name = "CitizenId")
    private CitizenEntity CitizenID;

    @Id
    @Column(name = "VotingId")
    private VotingEntity VotingID;

    public VotingControlEntity() {
    }


    public VotingControlEntity(CitizenEntity citizenID, VotingEntity votingID) {
        CitizenID = citizenID;
        VotingID = votingID;
    }

    public CitizenEntity getCitizenID() {
        return CitizenID;
    }

    public void setCitizenID(CitizenEntity citizenID) {
        CitizenID = citizenID;
    }

    public VotingEntity getVotingID() {
        return VotingID;
    }

    public void setVotingID(VotingEntity votingID) {
        VotingID = votingID;
    }
}
