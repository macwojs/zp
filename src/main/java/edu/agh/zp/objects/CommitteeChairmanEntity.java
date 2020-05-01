package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "CommitteeChairman")

public class CommitteeChairmanEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CommitteeChairman_chairmanID_seq")
    @SequenceGenerator(name = "CommitteeChairman_chairmanID_seq", sequenceName = "CommitteeChairman_chairmanID_seq", allocationSize = 1)
    @Column(name="chairmanID")
    private long chairmanID;


    @NotNull
    @ManyToOne
    @JoinColumn(name="memberID")
    private CommitteeMemberEntity memberID;

}