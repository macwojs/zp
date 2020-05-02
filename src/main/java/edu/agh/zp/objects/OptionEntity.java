package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "Option")

public class OptionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Option_optionID_seq")
    @SequenceGenerator(name = "Option_optionID_seq", sequenceName = "Option_optionID_seq", allocationSize = 1)
    @Column(name="optionID")
    private long optionID;


    @NotNull
    @Column(name="optionDescription")
    private String optionDescription;


}