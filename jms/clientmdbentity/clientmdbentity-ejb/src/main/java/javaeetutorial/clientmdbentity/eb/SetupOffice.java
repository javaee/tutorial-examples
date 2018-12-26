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

package javaeetutorial.clientmdbentity.eb;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The SetupOffice class implements the business methods of the entity.
 */
@Entity
public class SetupOffice implements Serializable {

    static final Logger logger = Logger.getLogger("SetupOffice");
    private static final long serialVersionUID = 1L;

    /**
     * no-argument constructor
     */
    public SetupOffice() {
    }

    /**
     * Constructor with two arguments
     *
     * @param newHireID employee ID (primary key)
     * @param name employee name
     */
    public SetupOffice(String newhireID, String name) {
        this.id = newhireID;
        this.name = name;
        this.equip = null;
        this.officeNum = -1;
    }

    /*
     * There should be a list of replies for each message being
     * joined.  This example is joining the work of separate
     * departments on the same original request, so it is all
     * right to have only one reply destination.  In theory, this
     * should be a set of destinations, with one reply for each
     * unique destination.
     */
    private String id;
    private String name;
    private int officeNum;
    private String equip;

    @Id
    public String getEmployeeId() {
        return id;
    }

    public void setEmployeeId(String id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return name;
    }

    public void setEmployeeName(String name) {
        this.name = name;
    }

    public int getOfficeNumber() {
        return officeNum;
    }

    public void setOfficeNumber(int officeNum) {
        this.officeNum = officeNum;
    }

    public String getEquipmentList() {
        return equip;
    }

    public void setEquipmentList(String equip) {
        this.equip = equip;
    }

    /**
     * The doEquipmentList method stores the assigned equipment in the database,
     * then determines if setup is complete.
     *
     * @param list assigned equipment
     *
     * @return true if setup is complete
     */
    public boolean doEquipmentList(String list) {

        boolean done;

        setEquipmentList(list);
        logger.log(Level.INFO, "SetupOffice.doEquipmentList: Equipment for "
                + "employeeId {0} is {1} (office number {2})",
                new Object[]{getEmployeeId(), getEquipmentList(),
                    getOfficeNumber()});
        done = checkIfSetupComplete();
        return done;
    }

    /**
     * The doOfficeNumber method stores the assigned office number in the
     * database, then determines if setup is complete.
     *
     * @param officeNum assigned office
     *
     * @return true if setup is complete
     */
    public boolean doOfficeNumber(int officeNum) {

        boolean done;

        setOfficeNumber(officeNum);
        logger.log(Level.INFO, "SetupOffice.doOfficeNumber: Office number for "
                + "employeeId {0} is {1} (equipment {2})",
                new Object[]{getEmployeeId(), getOfficeNumber(),
                    getEquipmentList()});
        done = checkIfSetupComplete();
        return done;
    }

    /**
     * The checkIfSetupComplete method determines whether both the office and
     * the equipment have been assigned. If so, it reports that the work of the
     * entity is done.
     *
     * @return true if setup is complete
     */
    private boolean checkIfSetupComplete() {
        boolean allDone = false;

        if ((getEquipmentList() != null) && (getOfficeNumber() != -1)) {
            logger.log(Level.INFO, "SetupOffice.checkIfSetupComplete:"
                    + " SCHEDULE employeeId={0}, Name={1} to be set up in"
                    + " office #{2} with {3}",
                    new Object[]{getEmployeeId(), getEmployeeName(),
                        getOfficeNumber(), getEquipmentList()});

            allDone = true;
        }
        return allDone;
    }
}
