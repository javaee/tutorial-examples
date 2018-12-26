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
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.Digits;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ian
 */
@NamedQueries({
    @NamedQuery(
        name = "Student.findAllStudents",
        query = "SELECT s FROM Student s " +
                "ORDER BY s.lastName"),
    @NamedQuery(
        name = "Student.findStudentsByStatus",
        query = "SELECT s FROM Student s " +
                "WHERE s.status = :status " +
                "ORDER BY s.lastName")
})
@Entity
@XmlRootElement(name = "Student")
@XmlAccessorType(XmlAccessType.FIELD)
public class Student extends Person implements Serializable {
    private static final long serialVersionUID = -6358383105437539027L;
    @ManyToMany(mappedBy = "students")
    protected List<TutoringSession> sessions;
    protected String school;
    @Digits(integer=2,
            fraction=0,
            message="{invalid.grade.level}")
    protected int grade;
    @ManyToMany
    @XmlTransient
    private List<Guardian> guardians;
    @Enumerated(EnumType.STRING)
    protected StatusType status = StatusType.OUT;
    @OneToMany(mappedBy="student", cascade=CascadeType.ALL)
    @XmlTransient
    protected List<StatusEntry> statusEntries;
    protected boolean active;


    public Student() {
        this.active = true;
        this.sessions = new ArrayList<>();
        this.statusEntries = new ArrayList<>();
        this.guardians = new ArrayList<>();
        this.addresses = new ArrayList<>();
    }

    /**
     * Get the value of statusEntries
     *
     * @return the value of statusEntries
     */
    public List<StatusEntry> getStatusEntries() {
        return statusEntries;
    }

    /**
     * Set the value of statusEntries
     *
     * @param statusEntries new value of statusEntries
     */
    public void setStatusEntries(List<StatusEntry> statusEntries) {
        this.statusEntries = statusEntries;
    }


    /**
     * @return the sessions
     */
    public List<TutoringSession> getSessions() {
        return sessions;
    }

    /**
     * @param sessions the sessions to set
     */
    public void setSessions(List<TutoringSession> sessions) {
        this.sessions = sessions;
    }

    /**
     * @return the school
     */
    public String getSchool() {
        return school;
    }

    /**
     * @param school the school to set
     */
    public void setSchool(String school) {
        this.school = school;
    }

    /**
     * @return the grade
     */
    public int getGrade() {
        return grade;
    }

    /**
     * @param grade the grade to set
     */
    public void setGrade(int grade) {
        this.grade = grade;
    }

    /**
     * @return the guardians
     */
    public List<Guardian> getGuardians() {
        return guardians;
    }

    /**
     * @param guardians the guardians to set
     */
    public void setGuardians(List<Guardian> guardians) {
        this.guardians = guardians;
    }
    /**
     * Get the value of status
     *
     * @return the value of status
     */
    public StatusType getStatus() {
        return status;
    }

    /**
     * Set the value of status
     *
     * @param status new value of status
     */
    public void setStatus(StatusType status) {
        this.status = status;
    }

    public String getStatusLabel() {
        return this.status.toString();
    }

    /**
     * Get the value of active
     *
     * @return the value of active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set the value of active
     *
     * @param active new value of active
     */
    public void setActive(boolean active) {
        this.active = active;
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
        if (!(object instanceof Student)) {
            return false;
        }
        Student other = (Student) object;
        if ((this.id == null && other.id != null) ||
                (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dukestutoring.entity.Student[id=" + id + "]";
    }
}
