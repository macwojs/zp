package edu.agh.zp.objects;

import edu.agh.zp.validator.ID;
import edu.agh.zp.validator.Password;
import edu.agh.zp.validator.Pesel;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;


@Entity(name = "Citizen")
@Password(groups = CitizenEntity.class)
public class CitizenEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "Citizen_citizenID_seq")
    @SequenceGenerator(name = "Citizen_citizenID_seq", sequenceName = "Citizen_citizenID_seq", allocationSize = 1)
    @NotNull
    @Column(name = "citizenID")
    private long citizenID;

    @NotNull
    @NotBlank (message = "Musisz podać hasło.")
    @Size (min = 8, message = "Hasło musi posiagać minimum 8 znaków")
    @Column(name="password")

    private String password;

    @NotNull
    @NotBlank(message = "Musisz podać email.")
    @Email (message = "Wprowadź poprawny adres email.")
    @Column(name="email")
    private String email;

    @NotNull
    @NotBlank (message = "Musisz podać imię.")
    @Column(name="name")
    private String name;

    @NotNull
    @NotBlank (message = "Musisz podać nazwisko.")
    @Column(name="surname")
    private String surname;

    @NotNull
    @NotBlank (message = "Musisz podać pesel.")
    @Pesel
    @Size(min = 11, max =11, message = "Pesel musi posiadać 11 cyfr.")
    @Column(name="pesel",length = 11)
    private String pesel;


    @NotBlank (message = "Musisz podać numer dowodu.")
    @ID
    @Size (min = 9, max =9, message = "Wprowadz numer dowodu w formacie ABC123456.")
    @Column(name="idNumber", length = 9)
    private String idNumber;

    @NotNull
    @Transient
    @NotBlank (message = "Musisz powtórzyć hasło.")
    private String repeatPassword;

    public CitizenEntity() {}

    public CitizenEntity(String password, String rp, String email, String name, String surname, String pesel, String idNumber) {
        this.password = password;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
        this.idNumber = idNumber;
        this.repeatPassword = rp;
    }

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

    @Override
    public String toString() {
        return "CitizenEntity{" +
                "citizenID=" + citizenID +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", pesel='" + pesel + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", repeatPassword='" + repeatPassword + '\'' +
                '}';
    }
}