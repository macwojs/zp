package edu.agh.zp.hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;

@Entity(name = "Document")

public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "docID")
    private long docID;

    @NotNull
    @Column(name="docName")
    private String docName;

    @NotNull
    @Column(name="docDescription")
    private String docDescription;

    @Column(name="pdfFile")
    private String pdfFilePath;

    @Column(name="validityFrom")
    private Date validityFrom;

    @Column(name="validityTo")
    private Date validityTo;

    @ManyToOne
    @NotNull
    @JoinColumn(name="docTypeID")
    private DocumentTypeEntity docTypeID;

    @ManyToOne
    @JoinColumn(name="docStatusID")
    private DocumentStatusEntity docStatusID;


}
