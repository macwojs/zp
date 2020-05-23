package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity(name = "Vote")

public class VoteEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Vote_VoteID_seq")
    @SequenceGenerator(name = "Vote_VoteID_seq", sequenceName = "Vote_VoteID_seq", allocationSize = 1)
    @NotNull
    @Column(name = "VoteID")
    private long VoteID;

    @NotNull
    @Column(name = "voteTimestamp")
    private Timestamp voteTimestamp;

    @ManyToOne
    @JoinColumn(name="citizenID")
    private CitizenEntity citizenID;

    @ManyToOne
    @NotNull
    @JoinColumn(name="optionID")
    private OptionEntity optionID;

    @ManyToOne
    @NotNull
    @JoinColumn(name="votingID")
    private VotingEntity votingID;

    public VoteEntity() {
    }


    public long getVoteID() {
        return VoteID;
    }

    public Timestamp getVoteTimestamp() {
        return voteTimestamp;
    }

    public void setVoteTimestamp(Timestamp voteTimestamp) {
        this.voteTimestamp = voteTimestamp;
    }

    public CitizenEntity getCitizenID() {
        return citizenID;
    }

    public void setCitizenID(CitizenEntity docTypeID) {
        this.citizenID = docTypeID;
    }

    public OptionEntity getOptionID() {
        return optionID;
    }

    public void setOptionID(OptionEntity optionID) {
        this.optionID = optionID;
    }

    public VotingEntity getVotingID() {
        return votingID;
    }

    public void setVotingID(VotingEntity votingID) {
        this.votingID = votingID;
    }
}