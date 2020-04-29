package edu.agh.zp.objects;

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
    private String funName;


    @Override
    public String toString(){
        return "ID: " + functionID + "\nname: " + funName + "\n";
    }

    public FunctionEntity(){}

    public FunctionEntity(String name){
        this.funName = name;
    }

    public void SetName(String name){
        this.funName = name;
    }

    public String GetName(){
        return this.funName;
    }


}