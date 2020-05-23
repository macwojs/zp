package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "Set")

public class SetEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Set_setID_seq")
    @SequenceGenerator(name = "Set_setID_seq", sequenceName = "Set_setID_seq", allocationSize = 1, initialValue = 3)
    @Column(name="setID")
    private long setID_column;


    @NotNull
    @Column(name="setName")
    private String setName_column;


    public SetEntity(@NotNull String setName_column) {
        this.setName_column = setName_column;
    }

    public SetEntity() {
        this.setName_column = setName_column;
    }

    public long getSetID_column() {
        return setID_column;
    }

    public String getSetName_column() {
        return setName_column;
    }

    public void setSetName_column(String setName_column) {
        this.setName_column = setName_column;
    }
}