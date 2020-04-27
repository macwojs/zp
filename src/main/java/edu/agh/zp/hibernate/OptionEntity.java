package edu.agh.zp.hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "Option")

public class OptionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="optionID")
    private long optionID;


    @NotNull
    @Column(name="optionDescription")
    private String optionDescription;


}