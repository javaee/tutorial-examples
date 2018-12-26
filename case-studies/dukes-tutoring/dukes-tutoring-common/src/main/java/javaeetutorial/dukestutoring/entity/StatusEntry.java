/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
