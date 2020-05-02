package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "CommitteeMember")

public class CommitteeMemberEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CommitteeMember_memberID_seq")
    @SequenceGenerator(name = "CommitteeMember_memberID_seq", sequenceName = "CommitteeMember_memberID_seq", allocationSize = 1)
    @Column(name="memberID")
    private long memberID;


    @NotNull
    @ManyToOne
    @JoinColumn(name="politicianID")
    private PoliticianEntity politicianID;


    @NotNull
    @ManyToOne
    @JoinColumn(name="comID")
    private CommitteeEntity comID;

}