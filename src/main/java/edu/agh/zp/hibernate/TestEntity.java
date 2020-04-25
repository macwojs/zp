package edu.agh.zp.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "test_hibernate")

public class TestEntity {
    @Id
    @GeneratedValue
    private int id;

    @Column(name="name")
    private String name;

    @Column(name="some_number", length = 13)
    private String some_number;

    @Override
    public String toString() {
        return "EmployeeEntity [id=" + id + ", Name=" + name +
                ", number=" + some_number;
    }

}
