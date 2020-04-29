package edu.agh.zp.objects;

import org.springframework.data.annotation.Transient;

import java.util.Objects;

public class User {
    private String email;
    private String name;
    private String surname;
    private String pesel;
    private String idnumber;
    private String password;
    @Transient // keyword to not push field to the database
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
