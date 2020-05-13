package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "OptionSet")

public class OptionSetEntity implements Serializable {

    @EmbeddedId OptionSetID optionSetID;

    public OptionSetEntity(OptionEntity optionID, SetEntity setID_column) {
        this.optionSetID = new OptionSetID(optionID,setID_column);
    }

    public OptionSetEntity() {
        this.optionSetID = new OptionSetID();
    }

    public OptionEntity getOptionID() {
        return this.optionSetID.getOptionID();
    }

    public void setOptionID(OptionEntity optionID) {
        this.optionSetID.setOptionID(optionID);
    }

    public SetEntity getSetIDcolumn() {
        return this.optionSetID.getSetIDcolumn();
    }

    public void setSetIDcolumn(SetEntity setID_column) {
        this.optionSetID.setSetIDcolumn(setID_column);
    }

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
    private SetEntity setIDcolumn;

    public OptionSetID(@NotNull OptionEntity optionID, @NotNull SetEntity setIDcolumn) {
        this.optionID = optionID;
        this.setIDcolumn = setIDcolumn;
    }

    public OptionSetID() {}

    public OptionEntity getOptionID() {
        return optionID;
    }

    public void setOptionID(OptionEntity optionID) {
        this.optionID = optionID;
    }

    public SetEntity getSetIDcolumn() {
        return setIDcolumn;
    }

    public void setSetIDcolumn(SetEntity setID_column) {
        this.setIDcolumn = setID_column;
    }
}