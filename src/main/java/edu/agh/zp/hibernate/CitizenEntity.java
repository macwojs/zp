package edu.agh.zp.hibernate;

import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;

@Entity(name = "Citizen")

public class CitizenEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    @Column(name = "CitizenID")
    private long CitizenID;

    @NotNull
    @UniqueElements
    @Column(name="hash")
    private String hash;

    @NotNull
    @UniqueElements
    @Column(name="pesel",length = 11)
    private String pesel;

    @UniqueElements
    @Column(name="idNumber", length = 9)
    private String idNumber;


}