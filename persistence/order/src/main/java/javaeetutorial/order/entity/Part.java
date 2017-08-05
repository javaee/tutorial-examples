/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.order.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.DATE;

@IdClass(PartKey.class)
@Entity
@Table(name="PERSISTENCE_ORDER_PART")
@SecondaryTable(name="PERSISTENCE_ORDER_PART_DETAIL", pkJoinColumns={
   @PrimaryKeyJoinColumn(name="PARTNUMBER", referencedColumnName="PARTNUMBER"),
   @PrimaryKeyJoinColumn(name="REVISION", referencedColumnName="REVISION")
})
@NamedQuery(
    name="findAllParts",
    query="SELECT p FROM Part p " +
          "ORDER BY p.partNumber"
)
public class Part implements Serializable {
    private static final long serialVersionUID = -3082087016342644227L;
    private String partNumber;
    private int revision;
    private String description;
    private Date revisionDate;
    private Serializable drawing;
    private String specification;
    private Part bomPart;
    private List<Part> parts;
    private VendorPart vendorPart;
    
    public Part() {}
    
    public Part(String partNumber,
            int revision,
            String description,
            Date revisionDate,
            String specification,
            Serializable drawing) {
        this.partNumber = partNumber;
        this.revision = revision;
        this.description = description;
        this.revisionDate = revisionDate;
        this.specification = specification;
        this.drawing = drawing;
        this.parts = new ArrayList<>();
    }

    @Id
    @Column(nullable=false)
    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    @Id
    @Column(nullable=false)
    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Temporal(DATE)
    public Date getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(Date revisionDate) {
        this.revisionDate = revisionDate;
    }

    @Column(table="PERSISTENCE_ORDER_PART_DETAIL")
    @Lob
    public Serializable getDrawing() {
        return drawing;
    }

    public void setDrawing(Serializable drawing) {
        this.drawing = drawing;
    }

    @Column(table="PERSISTENCE_ORDER_PART_DETAIL")
    @Lob
    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name="BOMPARTNUMBER", referencedColumnName="PARTNUMBER"),
        @JoinColumn(name="BOMREVISION", referencedColumnName="REVISION")
    })
    public Part getBomPart() {
        return bomPart;
    }

    public void setBomPart(Part bomPart) {
        this.bomPart = bomPart;
    }

    @OneToMany(mappedBy="bomPart")
    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    @OneToOne(mappedBy="part")
    public VendorPart getVendorPart() {
        return vendorPart;
    }

    public void setVendorPart(VendorPart vendorPart) {
        this.vendorPart = vendorPart;
    }
}
