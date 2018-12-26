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

package javaeetutorial.dukestutoring.web;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaeetutorial.dukestutoring.ejb.AdminBean;
import javaeetutorial.dukestutoring.entity.Guardian;
import javaeetutorial.dukestutoring.entity.Student;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Named
@RequestScoped
public class GuardianBean {

    private static final Logger logger = 
            Logger.getLogger("dukestutoring.web.GuardianBean");
    @Inject
    private AdminBean adminBean;
    private Guardian guardian;
    @NotNull
    private String firstName;
    private String middleName;
    @NotNull
    private String lastName;
    private String nickname;
    private String suffix;
    protected String email;
    @Pattern(regexp = "\\(\\d{3}\\) \\d{3}-\\d{4}",
            message = "{invalid.phonenumber}")
    protected String homePhone;
    @Pattern(regexp = "\\(\\d{3}\\) \\d{3}-\\d{4}",
            message = "{invalid.phonenumber}")
    protected String mobilePhone;
    protected Guardian selectedGuardian;
    protected List<Guardian> selectedGuardians;
    protected List<Guardian> allGuardians;

    /**
     * Creates a new instance of GuardianBean
     */
    public GuardianBean() {
    }

    public Guardian getGuardian() {
        return guardian;
    }

    public void setGuardian(Guardian guardian) {
        this.guardian = guardian;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public List<Guardian> getAllGuardians() {
        if (allGuardians == null) {
            try {
                this.allGuardians = adminBean.getAllGuardians();
            } catch (Exception ex) {
                logger.log(Level.SEVERE, 
                        "adminBean.getAllGuardians returned an error{0}", 
                        ex.getMessage());
            }
        }
        return this.allGuardians;

    }

    /**
     * Get the list of guardians except those already assigned to this student
     *
     * @param student the student we want a list of possible new guardians for
     * @return the list of possible guardians
     */
    public List<Guardian> getAllOtherGuardians(Student student) {
        List<Guardian> otherGuardians = getAllGuardians();
        for (Guardian guardian : student.getGuardians()) {
            otherGuardians.remove(guardian);
        }
        return otherGuardians;
    }

    public void setAllGuardians(List<Guardian> allGuardians) {
        this.allGuardians = allGuardians;
    }

    public List<Guardian> getSelectedGuardians() {
        return selectedGuardians;
    }

    public void setSelectedGuardians(List<Guardian> selectedGuardians) {
        this.selectedGuardians = selectedGuardians;
    }

    public Guardian getSelectedGuardian() {
        return selectedGuardian;
    }

    public void setSelectedGuardian(Guardian selectedGuardian) {
        this.selectedGuardian = selectedGuardian;
    }

    public String submit(Student student) {

        this.guardian = adminBean.createGuardian(firstName, middleName,
                lastName, nickname, suffix, email, homePhone, mobilePhone, student);
        return "createdGuardian";
    }
}
