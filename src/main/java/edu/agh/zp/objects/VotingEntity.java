package edu.agh.zp.objects;

import edu.agh.zp.validator.DateAfterNow;
import edu.agh.zp.validator.TimeAfterNow;
import edu.agh.zp.validator.TimeOrder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;



@Entity(name = "Voting")
@TimeOrder
//@TimeAfterNow
public class VotingEntity implements Serializable {

    public enum TypeOfVoting{
        PREZYDENT, SEJM, SENAT, REFERENDUM
    }

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Voting_votingID_seq")
    @SequenceGenerator(name = "Voting_votingID_seq", sequenceName = "Voting_votingID_seq", allocationSize = 1)
    @Column(name = "votingID")
    private long votingID;

    @DateAfterNow
    @Column(name="votingDate")
    private Date votingDate;

    @Column(name="votingType")
    private TypeOfVoting votingType;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name="openVoting")
    private Time openVoting;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name="closeVoting")
    private Time closeVoting;

    @Column(name="votingDescription")
    private String votingDescription;

    @ManyToOne
//    @NotNull
    @JoinColumn(name="setID")
    private SetEntity setID_column;

    @ManyToOne
    @JoinColumn(name="documentID")
    private DocumentEntity documentID;

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    @Transient
    private String open;

    @Transient
    private String close;

    public VotingEntity(){}

    public VotingEntity( Date date, Time open, Time close, SetEntity setID, DocumentEntity document,TypeOfVoting type, String VotingDesc){
        this.votingDate = date;
        this.openVoting = open;
        this.closeVoting = close;
        this.setID_column = setID;
        this.documentID = document;
        this.votingType = type;
        this.votingDescription = VotingDesc;
    }
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

    public TypeOfVoting getVotingType() {
        return votingType;
    }

    public void setVotingType( TypeOfVoting votingType ) {
        this.votingType = votingType;
    }

    public Time getOpenVoting() {
        return openVoting;
    }

    public void setOpenVoting( Time openVoting ) {
        this.openVoting = openVoting;
    }

    public Time getCloseVoting() {
        return closeVoting;
    }

    public void setCloseVoting( Time closeVoting ) {
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