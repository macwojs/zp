package edu.agh.zp.hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "Politician")

public class PoliticianEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    @Column(name = "PoliticianID")
    private long PoliticianID;

    @NotNull
    @Column(name="name")
    private String name;

    @NotNull
    @Column(name="surname")
    private String surname;

    @OneToOne
    @NotNull
    @JoinColumn(name="CitizenID")
    private CitizenEntity CitizenID;


}
