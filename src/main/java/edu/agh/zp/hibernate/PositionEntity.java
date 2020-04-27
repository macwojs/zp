package edu.agh.zp.hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "Position")

public class PositionEntity implements Serializable {

    @Id
    @ManyToOne
    @NotNull
    @JoinColumn(name="politicianID")
    private PoliticianEntity politicianID;


    @Id
    @ManyToOne
    @NotNull
    @JoinColumn(name="functionID")
    private FunctionEntity functionID;


}