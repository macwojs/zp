package edu.agh.zp.hibernate;

import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity(name = "Vote")

public class VoteEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    @Column(name = "VoteID")
    private long CitizenID;

    @NotNull
    @Column(name = "voteTimestamp")
    private Timestamp voteTimestamp;

    @ManyToOne
    @NotNull
    @JoinColumn(name="citizenID")
    private CitizenEntity docTypeID;

    @ManyToOne
    @NotNull
    @JoinColumn(name="optionID")
    private OptionEntity optionID;

    @ManyToOne
    @NotNull
    @JoinColumn(name="votingID")
    private VotingEntity votingID;



}