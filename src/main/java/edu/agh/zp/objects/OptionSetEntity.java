package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "OptionSet")
@IdClass(IdOptionSet.class)
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
    private SetEntity setIDcolumn;



    public OptionSetEntity(OptionEntity optionID, SetEntity setID_column) {
        this.optionID = optionID;
        this.setIDcolumn = setID_column;
    }

    public OptionSetEntity() {
    }

    public OptionEntity getOptionID() {
        return this.optionID;
    }

    public void setOptionID(OptionEntity optionID) {
        this.optionID = optionID;
    }

    public SetEntity getSetIDcolumn() {
        return this.setIDcolumn;
    }

    public void setSetIDcolumn(SetEntity setID_column) {
        this.setIDcolumn = setID_column;
    }
}