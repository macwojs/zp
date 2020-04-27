package edu.agh.zp.hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.Timestamp;

@Entity(name = "Voting")

public class VotingEntity {

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


}