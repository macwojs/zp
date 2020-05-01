package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
@Entity(name = "\"Citizen\"")

public class CitizenEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Citizen_citizenID_seq")
    @SequenceGenerator(name = "Citizen_citizenID_seq", sequenceName = "Citizen_citizenID_seq", allocationSize = 1)
    @NotNull
    @Column(name = "\"citizenID\"")
    private long citizenID;

    @NotNull
    @Column(name="\"password\"")
    private String password;

    @NotNull
    @Email
    @Column(name="email")
    private String email;

    @NotNull
    @Column(name="name")
    private String name;

    @Column(name="\"surname\"")
    private String surname;

    @NotNull
    @Column(name="pesel",length = 11)
    private String pesel;

    @Column(name="\"idNumber\"", length = 9)
    private String idNumber;

    @Transient
    private String repeatPassword;

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public CitizenEntity() {

    }

    public long getCitizenID() {
        return citizenID;
    }

    public String getPassword() {
        return password;
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
        return getPassword().equals(that.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPassword());
    }

    public void setPassword(String hash) {
        this.password = hash;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }


}