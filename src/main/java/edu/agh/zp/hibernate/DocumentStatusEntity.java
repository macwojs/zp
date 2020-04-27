package edu.agh.zp.hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "DocumentStatus")

public class DocumentStatusEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="docStatusID")
    private long docTypeID;


    @NotNull
    @Column(name="docStatusName")
    private String docTypeName;


}