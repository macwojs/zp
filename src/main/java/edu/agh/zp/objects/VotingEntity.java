package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;



@Entity(name = "Voting")

public class VotingEntity implements Serializable {

    public enum TypeOfVoting{
        PREZYDENT, SEJM, SENAT, REFERENDUM, PARLAMENT
    }

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Voting_votingID_seq")
    @SequenceGenerator(name = "Voting_votingID_seq", sequenceName = "Voting_votingID_seq", allocationSize = 1)
    @Column(name = "votingID")
    private long votingID;

    @Column(name="votingDate")
    private Date votingDate;

    @Column(name="votingType")
    private TypeOfVoting votingType;

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


    public void OnSave(){}

}

//TODO...