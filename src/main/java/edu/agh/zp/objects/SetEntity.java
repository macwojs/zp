package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "Set")

public class SetEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Set_setID_seq")
    @SequenceGenerator(name = "Set_setID_seq", sequenceName = "Set_setID_seq", allocationSize = 1)
    @Column(name="setID")
    private long setID;


    @NotNull
    @Column(name="setName")
    private String setName;


    public SetEntity(@NotNull String setName) {
        this.setName = setName;
    }

    public SetEntity() {
    }

    public long getSetID() {
        return setID;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }
}