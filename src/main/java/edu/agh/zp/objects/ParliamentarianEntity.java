package edu.agh.zp.objects;

import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "Parliamentarian")

public class ParliamentarianEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Parliamentarian_parliamentarianID_seq")
    @SequenceGenerator(name = "Parliamentarian_parliamentarianID_seq", sequenceName = "Parliamentarian_parliamentarianID_seq", allocationSize = 1)
    @NotNull
    @Column(name = "parliamentarianID")
    private long parliamentarianID;

    @NotNull
    @Column(name="idCardNumber", unique = true)
    private String idCardNumber;

    @NotNull
    @Column(name="politicalGroup")
    private String politicalGroup;

    @NotNull
    @Column(name="chamberOfDeputies")
    private String chamberOfDeputies;

    @OneToOne
    @NotNull
    @JoinColumn(name="politicianID")
    private PoliticianEntity politicianID;

    public ParliamentarianEntity() {
    }

    public ParliamentarianEntity(String Card, String group, String chamber, PoliticianEntity politician) {
    this.idCardNumber = Card;
    this.politicalGroup = group;
    this.chamberOfDeputies = chamber;
    this.politicianID = politician;
    }

    public long getParliamentarianID() {
        return parliamentarianID;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public void setPoliticalGroup(String politicalGroup) {
        this.politicalGroup = politicalGroup;
    }

    public void setChamberOfDeputies(String chamberOfDeputies) {
        this.chamberOfDeputies = chamberOfDeputies;
    }


}
