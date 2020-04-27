package edu.agh.zp.hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "Function")

public class FunctionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="functionID")
    private long functionID;


    @NotNull
    @Column(name="funName")
    private String setName_column;


}