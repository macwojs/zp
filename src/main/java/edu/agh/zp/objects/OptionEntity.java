package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "Option")

public class OptionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Option_optionID_seq")
    @SequenceGenerator(name = "Option_optionID_seq", sequenceName = "Option_optionID_seq", allocationSize = 1, initialValue = 6)
    @Column(name="optionID")
    private long optionID;


    @NotNull
    @Column(name="optionDescription")
    private String optionDescription;

    public OptionEntity() {
    }

    public OptionEntity( @NotNull String optionDescription) {
        this.optionDescription = optionDescription;
    }


    public long getOptionID() {
        return optionID;
    }

    public String getOptionDescription() {
        return optionDescription;
    }

    public void setOptionDescription(String optionDescription) {
        this.optionDescription = optionDescription;
    }
}