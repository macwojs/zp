package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "Politician")

public class PoliticianEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Politician_PoliticianID_seq")
    @SequenceGenerator(name = "Politician_PoliticianID_seq", sequenceName = "Politician_PoliticianID_seq", allocationSize = 1)
    @NotNull
    @Column(name = "PoliticianID")
    private long PoliticianID;

    @OneToOne
    @NotNull
    @JoinColumn(name="CitizenID")
    private CitizenEntity CitizenID;

    public PoliticianEntity() {
    }

    public PoliticianEntity(CitizenEntity citizen) {
        this.CitizenID = citizen;
    }

    public long getPoliticianID() {
        return PoliticianID;
    }

    public CitizenEntity getCitizenID() {
        return CitizenID;
    }

    public void setCitizenID(CitizenEntity citizenID) {
        CitizenID = citizenID;
    }
}
