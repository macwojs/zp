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


    public void setDocStatusID( long docStatusID ) {
        this.docStatusID = docStatusID;
    }

    public long getDocStatusID(  ) {
        return this.docStatusID;
    }

    public void setDocStatusName( String docStatusName ) {
        this.docStatusName = docStatusName;
    }

    public String getDocStatusName(  ) {
        return this.docStatusName;
    }
}