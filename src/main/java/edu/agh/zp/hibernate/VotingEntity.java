package edu.agh.zp.hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;


@Entity(name = "Voting")

public class VotingEntity implements Serializable {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    public Timestamp GetEnd(){ return closeVoting;}

    public Timestamp GetStart(){ return openVoting;}

    public void OnSave(){}

}

//TODO...