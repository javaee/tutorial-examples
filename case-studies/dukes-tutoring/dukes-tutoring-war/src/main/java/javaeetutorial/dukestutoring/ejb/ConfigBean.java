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
package javaeetutorial.dukestutoring.ejb;

import javaeetutorial.dukestutoring.entity.Administrator;
import javaeetutorial.dukestutoring.entity.Guardian;
import javaeetutorial.dukestutoring.entity.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;

/**
 *
 * @author ian
 */
@Singleton
@Startup
public class ConfigBean {

    @PersistenceContext
    private EntityManager em;
    private CriteriaBuilder cb;
    @EJB
    private AdminBean adminBean;
    @EJB
    private RequestBean requestBean;
    private static final Logger logger = Logger.getLogger("dukestutoring.ejb.ConfigBean");

    /**
     * Creates a new tutoring session every day at midnight.
     */
    @Schedule(dayOfWeek = "*")
    public void createTutoringSession() {
        logger.info("Creating today's session in ConfigBean");
        // Create a new tutoring session
        requestBean.getTodaysSession();
    }
    
    @Schedule(dayOfWeek = "Sun-Thu", hour="20")
    public void cleanUpTutoringSession() {
        // Check out students who weren't checked out for whatever reason
        requestBean.checkOutAllStudents();
    }

    @PostConstruct
    public void init() {
        requestBean.getTodaysSession();

        cb = em.getCriteriaBuilder();
        // create Maeby
        logger.info("Creating Maeby entity");
        Student maeby = new Student();
        maeby.setLastName("Fünke"); 
        maeby.setFirstName("Maeby");
        maeby.setGrade(10);
        maeby.setSchool("Sunshine Academy");
        
        // create Maeby's dad
        Guardian tobias = new Guardian();
        tobias.setFirstName("Tobias");
        tobias.setLastName("Fünke");
        tobias.setEmail("tobias@example.com");
        tobias.setPassword("javaee");
        
        // create Maeby's mom
        Guardian lindsey = new Guardian();
        lindsey.setFirstName("Lindsey");
        lindsey.setLastName("Fünke");
        lindsey.setEmail("lindsey@example.com");
        lindsey.setPassword("javaee");

        // create George Michael
        logger.info("Creating George Michael entity");
        Student georgeMichael = new Student();
        georgeMichael.setLastName("Bluth");
        georgeMichael.setFirstName("George");
        georgeMichael.setMiddleName("Michael");
        georgeMichael.setGrade(10);
        georgeMichael.setSchool("Huntington Beach High School");

        // create GOB
        logger.info("Creating Gob entity");
        Student gob = new Student();
        gob.setLastName("Bluth");
        gob.setFirstName("George");
        gob.setMiddleName("Oscar");
        gob.setNickname("Gob");
        gob.setGrade(12);
        gob.setSchool("Magician's Alliance Institute");
        
        // create Buster
        logger.info("Creating Buster entity");
        Student buster = new Student();
        buster.setFirstName("Byron");
        buster.setLastName("Bluth");
        buster.setNickname("Buster");
        buster.setGrade(11);
        buster.setSchool("Milford Academy");
        
        // create Lucille
        logger.info("Creating Lucille entity");
        Guardian lucille = new Guardian();
        lucille.setFirstName("Lucille");
        lucille.setLastName("Bluth");
        lucille.setEmail("lucille@example.com");
        lucille.setPassword("javaee");

        logger.info("Calling createStudent() on Maeby");
        String result = adminBean.createStudent(maeby);
        logger.info("Calling createStudent() on George Michael");
        result = adminBean.createStudent(georgeMichael);
        logger.info("Calling createStudent() on Gob");
        result = adminBean.createStudent(gob);
        
        logger.info("Calling createGuardian() for Maeby's parents");
        adminBean.createGuardian(tobias, maeby);
        adminBean.createGuardian(lindsey, maeby);
        
        logger.info("Calling createGuardian() for Buster and GOB's mom");
        List<Student> lucilleKids = new ArrayList<>();
        lucilleKids.add(gob);
        lucilleKids.add(buster);
        adminBean.createGuardianWithList(lucille, lucilleKids);

        logger.info("Checking in Maeby and George Michael");
        requestBean.checkIn(maeby);
        requestBean.checkIn(georgeMichael);

        // create admin
        Administrator admin = new Administrator();
        admin.setFirstName("Admin");
        admin.setLastName("Administrator");
        admin.setEmail("admin@example.com");
        admin.setPassword("javaee");
        result = adminBean.createAdministrator(admin);

    }
}
