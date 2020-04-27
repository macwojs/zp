package edu.agh.zp.hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "DocumentType")

public class DocumentTypeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="docTypeID")
    private long docTypeID;


    @NotNull
    @Column(name="docTypeName")
    private String docTypeName;


}
