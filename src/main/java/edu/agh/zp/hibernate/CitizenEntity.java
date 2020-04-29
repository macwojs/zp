package edu.agh.zp.hibernate;

import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Entity(name = "\"Citizen\"")

public class CitizenEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "\"citizenID\"")
    private long citizenID;

    @NotNull
    @Column(name="\"userKey\"")
    private String hash;

    @NotNull
    @Column(name="pesel",length = 11)
    private String pesel;

    @Column(name="\"idNumber\"", length = 9)
    private String idNumber;

    public CitizenEntity() {

    }

    public long getCitizenID() {
        return citizenID;
    }

    public String getHash() {
        return hash;
    }

    public String getPesel() {
        return pesel;
    }

    public String getIdNumber() {
        return idNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CitizenEntity)) return false;
        CitizenEntity that = (CitizenEntity) o;
        return getHash().equals(that.getHash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHash());
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }


}