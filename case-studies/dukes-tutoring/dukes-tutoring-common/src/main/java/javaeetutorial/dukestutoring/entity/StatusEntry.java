/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.dukestutoring.entity;

import javaeetutorial.dukestutoring.util.StatusType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ian
 */
@Entity
@XmlRootElement(name = "StatusEntry")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatusEntry implements Serializable {
    private static final long serialVersionUID = -4577867285308070101L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private StatusType currentStatus;
    @ManyToOne
    @XmlTransient
    private Student student;
    @ManyToOne
    @XmlTransient
    private TutoringSession tutoringSession;
    @Temporal(value = javax.persistence.TemporalType.TIMESTAMP)
    private Calendar statusDate;

    public StatusEntry() {

    }

    public StatusEntry(StatusType status, Student student, TutoringSession session) {
        this.setCurrentStatus(status);
        this.setStudent(student);
        this.setTutoringSession(session);
        this.setStatusDate(Calendar.getInstance());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StatusEntry)) {
            return false;
        }
        StatusEntry other = (StatusEntry) object;
        if ((this.id == null && other.id != null) ||
                (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dukestutoring.entity.StatusEntry[id=" + id + "]";
    }

    /**
     * @return the currentStatus
     */
    public StatusType getCurrentStatus() {
        return currentStatus;
    }

    /**
     * @param currentStatus the currentStatus to set
     */
    public void setCurrentStatus(StatusType currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getCurrentStatusLabel() {
        return currentStatus.toString();
    }

    /**
     * @return the student
     */
    public Student getStudent() {
        return student;
    }

    /**
     * @param student the student to set
     */
    public void setStudent(Student student) {
        this.student = student;
    }

    /**
     * @return the tutoringSession
     */
    public TutoringSession getTutoringSession() {
        return tutoringSession;
    }

    /**
     * @param tutoringSession the tutoringSession to set
     */
    public void setTutoringSession(TutoringSession tutoringSession) {
        this.tutoringSession = tutoringSession;
    }

    /**
     * @return the statusDate
     */
    public Calendar getStatusDate() {
        return statusDate;
    }

    /**
     * @param statusDate the statusDate to set
     */
    public void setStatusDate(Calendar statusDate) {
        this.statusDate = statusDate;
    }

    public String getFormattedStatusDate() {
        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
        return df.format(this.statusDate.getTime());
    }

}
