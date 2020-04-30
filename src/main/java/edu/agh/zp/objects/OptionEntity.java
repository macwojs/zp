package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "Option")

public class OptionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="optionID")
    private long optionID;


    @NotNull
    @Column(name="optionDescription")
    private String optionDescription;


}