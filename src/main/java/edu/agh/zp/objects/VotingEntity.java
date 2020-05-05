package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;


@Entity(name = "Voting")

public class VotingEntity implements Serializable {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Voting_votingID_seq")
    @SequenceGenerator(name = "Voting_votingID_seq", sequenceName = "Voting_votingID_seq", allocationSize = 1)
    @Column(name = "votingID")
    private long votingID;

    @Column(name="votingDate")
    private Date votingDate;


    @Column(name="openVoting")
    private Timestamp openVoting;

    @Column(name="closeVoting")
    private Timestamp closeVoting;


    @Column(name="votingDescription")
    private String votingDescription;

    @ManyToOne
    @NotNull
    @JoinColumn(name="setID")
    private SetEntity setID_column;

    @ManyToOne
    @JoinColumn(name="documentID")
    private DocumentEntity documentID;

    public VotingEntity(){}

    public VotingEntity(Date date, Timestamp open, Timestamp close,SetEntity setID_column,DocumentEntity documentID){}

    public void SetTime(Date date, Timestamp open, Timestamp close){
        this.openVoting = open;
        this.closeVoting = close;
        this.votingDate = date;
    }

    public void OnSave(){}


    public long getVotingID() {
        return votingID;
    }

    public void setVotingID( long votingID ) {
        this.votingID = votingID;
    }

    public Date getVotingDate() {
        return votingDate;
    }

    public void setVotingDate( Date votingDate ) {
        this.votingDate = votingDate;
    }

    public Timestamp getOpenVoting() {
        return openVoting;
    }

    public void setOpenVoting( Timestamp openVoting ) {
        this.openVoting = openVoting;
    }

    public Timestamp getCloseVoting() {
        return closeVoting;
    }

    public void setCloseVoting( Timestamp closeVoting ) {
        this.closeVoting = closeVoting;
    }

    public String getVotingDescription() {
        return votingDescription;
    }

    public void setVotingDescription( String votingDescription ) {
        this.votingDescription = votingDescription;
    }

    public SetEntity getSetID_column() {
        return setID_column;
    }

    public void setSetID_column( SetEntity setID_column ) {
        this.setID_column = setID_column;
    }

    public DocumentEntity getDocumentID() {
        return documentID;
    }

    public void setDocumentID( DocumentEntity documentID ) {
        this.documentID = documentID;
    }
}

//TODO...