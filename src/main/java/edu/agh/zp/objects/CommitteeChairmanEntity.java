package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "CommitteeChairman")

public class CommitteeChairmanEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="chairmanID")
    private long chairmanID;


    @NotNull
    @ManyToOne
    @JoinColumn(name="memberID")
    private CommitteeMemberEntity memberID;

}