package edu.agh.zp.hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "OptionSet")

public class OptionSetEntity implements Serializable {

    @EmbeddedId OptionSetID optionSetID;

}


@Embeddable
class OptionSetID implements Serializable {
    @ManyToOne
    @NotNull
    @JoinColumn(name="optionID")
    private OptionEntity optionID;


    @ManyToOne
    @NotNull
    @JoinColumn(name="setID")
    private SetEntity setID_column;
}