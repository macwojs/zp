package edu.agh.zp.objects;

import org.springframework.data.annotation.Transient;

import edu.agh.zp.validator.ID;
import edu.agh.zp.validator.Password;
import edu.agh.zp.validator.Pesel;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Password
public class User {

    @NotBlank (message = "Musisz podać email.")
    @Email (message = "Wprowadź poprawny adres email.")
    private String email;

    @NotBlank (message = "Musisz podać imię.")
    private String name;

    @NotBlank (message = "Musisz podać nazwisko.")
    private String surname;

    @NotBlank (message = "Musisz podać pesel.")
    @Pesel
    @Size (min = 11, max =11, message = "Pesel musi posiadać 11 cyfr.")
    private String pesel;

    @NotBlank (message = "Musisz podać numer d owodu.")
    @ID
    @Size (min = 9, max =9, message = "Wprowadz numer dowodu w formacie ABC123456.")
    private String idnumber;

    @NotBlank (message = "Musisz podać hasło.")
    @Size (min = 8, message = "Hasło musi posiadać minimum 8 znaków")
    private String password;

    @Transient // keyword to not push field to the database
    @NotBlank (message = "Musisz powtórzyć hasło.")
    private String repeat_password;

    public User(){}

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPesel() {
        return pesel;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public String getPassword() {
        return password;
    }

    public String getRepeat_password() {
        return repeat_password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRepeat_password(String repeat_password) {
        this.repeat_password = repeat_password;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPassword());
    }
}
