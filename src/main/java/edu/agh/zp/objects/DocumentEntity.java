package edu.agh.zp.objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;

@Entity(name = "Document")

public class DocumentEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Document_docID_seq")
    @SequenceGenerator(name = "Document_docID_seq", sequenceName = "Document_docID_seq", allocationSize = 1)
    @Column(name = "docID")
    private long docID;

    @NotNull(message = "Wprowadź nazwę dokumentu.")
    @Column(name="docName")
    private String docName;

    @NotNull(message = "Wprowadź opis dokumentu.")
    @Column(name="docDescription")
    private String docDescription;

    @Column(name="pdfFile")
    private String pdfFilePath;

    @Column(name="validityFrom")
    private Date validityFrom;

    @Column(name="validityTo")
    private Date validityTo;

    @ManyToOne
    @NotNull(message = "Musisz wybrać typ dokumentu.")
    @JoinColumn(name="docTypeID")
    private DocumentTypeEntity docTypeID;

    @ManyToOne
    @NotNull(message = "Musisz wybrać status dokumentu.")
    @JoinColumn(name="docStatusID")
    private DocumentStatusEntity docStatusID;

    public DocumentEntity() {
    }

    public DocumentEntity( long docID, @NotNull String docName, @NotNull String docDescription, String pdfFilePath, Date validityFrom, Date validityTo, @NotNull DocumentTypeEntity docTypeID, @NotNull DocumentStatusEntity docStatusID ) {
        this.docID = docID;
        this.docName = docName;
        this.docDescription = docDescription;
        this.pdfFilePath = pdfFilePath;
        this.validityFrom = validityFrom;
        this.validityTo = validityTo;
        this.docTypeID = docTypeID;
        this.docStatusID = docStatusID;
    }

    public long getDocID() {
        return docID;
    }

    public void setDocID( long docID ) {
        this.docID = docID;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName( String docName ) {
        this.docName = docName;
    }

    public String getDocDescription() {
        return docDescription;
    }

    public void setDocDescription( String docDescription ) {
        this.docDescription = docDescription;
    }

    public String getPdfFilePath() {
        return pdfFilePath;
    }

    public void setPdfFilePath( String pdfFilePath ) {
        this.pdfFilePath = pdfFilePath;
    }

    public Date getValidityFrom() {
        return validityFrom;
    }

    public void setValidityFrom( Date validityFrom ) {
        this.validityFrom = validityFrom;
    }

    public Date getValidityTo() {
        return validityTo;
    }

    public void setValidityTo( Date validityTo ) {
        this.validityTo = validityTo;
    }

    public DocumentTypeEntity getDocTypeID() {
        return docTypeID;
    }

    public void setDocTypeID( DocumentTypeEntity docTypeID ) {
        this.docTypeID = docTypeID;
    }

    public DocumentStatusEntity getDocStatusID() {
        return docStatusID;
    }

    public void setDocStatusID( DocumentStatusEntity docStatusID ) {
        this.docStatusID = docStatusID;
    }
}
