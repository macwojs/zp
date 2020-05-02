package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "Committee")

public class CommitteeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Committee_comID_seq")
    @SequenceGenerator(name = "Committee_comID_seq", sequenceName = "Committee_comID_seq", allocationSize = 1)
    @Column(name="comID")
    private long comID;


    @NotNull
    @Column(name="comName")
    private String comName;


    @Column(name="comDescription")
    private String comDescription;


}