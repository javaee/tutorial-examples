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

import javaeetutorial.dukestutoring.util.CalendarUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@NamedQueries({
    @NamedQuery(name="TutoringSession.findByDate",
                query="SELECT DISTINCT t " +
                      "FROM TutoringSession t " +
                      "WHERE t.sessionDate = :date ")
})
@XmlRootElement(name = "TutoringSession")
@XmlAccessorType(XmlAccessType.FIELD)
public class TutoringSession implements Serializable {
    private static final long serialVersionUID = -7046584503641718822L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @XmlTransient
    @ManyToMany()
    private final List<Student> students;
    @OneToMany(mappedBy = "tutoringSession", cascade=CascadeType.ALL)
    private List<StatusEntry> statusEntries;
    @Temporal(value = javax.persistence.TemporalType.DATE)
    private Calendar sessionDate;

    public TutoringSession() {
        Calendar cal = Calendar.getInstance();
        CalendarUtil.stripTime(cal);
        sessionDate = cal;
        students = new ArrayList<>();
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
        if (!(object instanceof TutoringSession)) {
            return false;
        }
        TutoringSession other = (TutoringSession) object;
        if ((this.id == null && other.id != null) ||
                (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dukestutoring.entity.Session[id=" + id + "]";
    }

    /**
     * @return the students
     */
    public List<Student> getStudents() {
        return students;
    }

    /**
     * @param students the students to set
     */
    public void setStudents(List<Student> students) {
        this.setStudents(students);
    }

    /**
     * @return the statusEntries
     */
    public List<StatusEntry> getStatusEntries() {
        return statusEntries;
    }

    /**
     * @param statusEntries the statusEntries to set
     */
    public void setStatusEntries(List<StatusEntry> statusEntries) {
        this.statusEntries = statusEntries;
    }

    /**
     * @return the sessionDate
     */
    public Calendar getSessionDate() {
        return sessionDate;
    }

    /**
     * @param sessionDate the sessionDate to set
     */
    public void setSessionDate(Calendar sessionDate) {
        this.sessionDate = sessionDate;
    }

}
