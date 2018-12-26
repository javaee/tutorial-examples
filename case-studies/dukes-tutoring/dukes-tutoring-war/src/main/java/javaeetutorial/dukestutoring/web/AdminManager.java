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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javaeetutorial.dukestutoring.web;

import javaeetutorial.dukestutoring.ejb.AdminBean;
import javaeetutorial.dukestutoring.entity.Address;
import javaeetutorial.dukestutoring.entity.Guardian;
import javaeetutorial.dukestutoring.entity.Student;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author ian
 */
@Named
@SessionScoped
public class AdminManager implements Serializable {
    private static final long serialVersionUID = 7090138834846165429L;
    protected Student currentStudent;
    protected Address currentAddress;
    protected Guardian currentGuardian;
    private Map<String, Integer> allGrades;

    @EJB
    private AdminBean adminBean;

    /**
     * Get the value of currentGuardian
     *
     * @return the value of currentGuardian
     */
    public Guardian getCurrentGuardian() {
        return currentGuardian;
    }

    /**
     * Set the value of currentGuardian
     *
     * @param currentGuardian new value of currentGuardian
     */
    public void setCurrentGuardian(Guardian currentGuardian) {
        this.currentGuardian = currentGuardian;
    }


    /**
     * Get the value of currentAddress
     *
     * @return the value of currentAddress
     */
    public Address getCurrentAddress() {
        return currentAddress;
    }

    /**
     * Set the value of currentAddress
     *
     * @param currentAddress new value of currentAddress
     */
    public void setCurrentAddress(Address currentAddress) {
        this.currentAddress = currentAddress;
    }


    /**
     * Get the value of currentStudent
     *
     * @return the value of currentStudent
     */
    public Student getCurrentStudent() {
        return currentStudent;
    }

    /**
     * Set the value of currentStudent
     *
     * @param currentStudent new value of currentStudent
     */
    public void setCurrentStudent(Student currentStudent) {
        this.currentStudent = currentStudent;
    }

    public void setCurrentStudentById(Long id) {
        this.currentStudent = adminBean.findStudentById(id);
    }

    /** Creates a new instance of AdminManager */
    public AdminManager() {
        this.currentAddress = new Address();
        this.currentGuardian = new Guardian();
        this.currentStudent = new Student();

        this.allGrades = new LinkedHashMap<>();
        this.allGrades.put("Kindergarten", new Integer(0));
        this.allGrades.put("First", new Integer(1));
        this.allGrades.put("Second", new Integer(2));
        this.allGrades.put("Third", new Integer(3));
        this.allGrades.put("Fourth", new Integer(4));
        this.allGrades.put("Fifth", new Integer(5));
        this.allGrades.put("Sixth", new Integer(6));
        this.allGrades.put("Seventh", new Integer(7));
        this.allGrades.put("Eighth", new Integer(8));
        this.allGrades.put("Ninth", new Integer(9));
        this.allGrades.put("Tenth", new Integer(10));
        this.allGrades.put("Eleventh", new Integer(11));
        this.allGrades.put("Twelfth", new Integer(12));
    }

    public String editStudent(Student student) {
        this.setCurrentStudent(student);
        return "editStudent";
    }

    public String createStudent() {
        this.setCurrentStudent(null);
        return "createStudent";
    }

    public String deleteStudent(Student student) {
        this.setCurrentStudent(student);
        return "deleteStudent";
    }

    public String editGuardian(Guardian guardian) {
        this.setCurrentGuardian(guardian);
        return "editGuardian";
    }

    public String createGuardian(Student student) {
        this.setCurrentStudent(student);
        this.setCurrentGuardian(null);
        return "createGuardian";
    }

    public String deleteGuardian(Guardian guardian) {
        this.setCurrentGuardian(guardian);
        return "deleteGuardian";
    }

    public String createStudentAddress(Student student) {
        this.setCurrentStudent(student);
        return "createAddress";
    }

    public String createGuardianAddress(Guardian guardian) {
        this.setCurrentGuardian(guardian);
        return "createAddress";
    }

    public String editAddress(Address address) {
        this.setCurrentAddress(address);
        return "editAddress";
    }

    public String deleteAddress(Address address) {
        this.setCurrentAddress(address);
        return "deleteAddress";
    }

    /**
     * @return the allGrades
     */
    public Map<String, Integer> getAllGrades() {
        return allGrades;
    }

    /**
     * @param allGrades the allGrades to set
     */
    public void setAllGrades(Map<String, Integer> allGrades) {
        this.allGrades = allGrades;
    }

}
