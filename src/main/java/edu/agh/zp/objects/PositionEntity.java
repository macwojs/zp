package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "Position")

public class PositionEntity implements Serializable {

   @EmbeddedId PositionID positionID;


}

@Embeddable
class PositionID implements Serializable{

    @ManyToOne
    @NotNull
    @JoinColumn(name="politicianID")
    private PoliticianEntity politicianID;


    @ManyToOne
    @NotNull
    @JoinColumn(name="functionID")
    private FunctionEntity functionID;
}