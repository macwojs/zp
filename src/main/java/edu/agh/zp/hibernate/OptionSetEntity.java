package edu.agh.zp.hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "OptionSet")

public class OptionSetEntity implements Serializable {

    @Id
    @ManyToOne
    @NotNull
    @JoinColumn(name="optionID")
    private OptionEntity optionID;


    @Id
    @ManyToOne
    @NotNull
    @JoinColumn(name="setID")
    private SetEntity setID_column;


}