/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
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
