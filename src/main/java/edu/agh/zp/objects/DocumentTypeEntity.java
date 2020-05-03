package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "DocumentType")

public class DocumentTypeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DocumentType_docTypeID_seq")
    @SequenceGenerator(name = "DocumentType_docTypeID_seq", sequenceName = "DocumentType_docTypeID_seq", allocationSize = 1)
    @Column(name="docTypeID")
    private long docTypeID;


    @NotNull
    @Column(name="docTypeName")
    private String docTypeName;

    @Override
    public String toString(){
        return "ID: " + docTypeID + "\nname: " + docTypeName + "\n";
    }

    public DocumentTypeEntity(){}

    public DocumentTypeEntity(String name){
        this.docTypeName = name;
    }

    public void setDocTypeID( long docTypeID ) {
        this.docTypeID = docTypeID;
    }

    public long getDocTypeID( ) {
        return this.docTypeID;
    }

    public void setDocTypeName( String docTypeName ) {
        this.docTypeName = docTypeName;
    }

    public String getDocTypeName( ) {
        return this.docTypeName;
    }
}
