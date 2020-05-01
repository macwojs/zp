package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "DocumentStatus")

public class DocumentStatusEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DocumentStatus_docStatusID_seq")
    @SequenceGenerator(name = "DocumentStatus_docStatusID_seq", sequenceName = "DocumentStatus_docStatusID_seq", allocationSize = 1)
    @Column(name="docStatusID")
    private long docStatusID;


    @NotNull
    @Column(name="docStatusName")
    private String docStatusName;

    @Override
    public String toString(){
        return "ID: " + docStatusID + "\nname: " + docStatusName + "\n";
    }

    public DocumentStatusEntity(){}

    public DocumentStatusEntity(String name){
        this.docStatusName = name;
    }

    public void SetName(String name){
        this.docStatusName = name;
    }

    public String GetName(){
        return this.docStatusName;
    }


}