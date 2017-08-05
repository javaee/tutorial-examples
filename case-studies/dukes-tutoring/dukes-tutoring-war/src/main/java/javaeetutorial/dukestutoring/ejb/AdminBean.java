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

import javaeetutorial.dukestutoring.entity.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.ws.rs.Path;

/**
 *
 * @author ian
 */
@Path("/tutoring/admin")
@Stateless
@Named
public class AdminBean {

    @PersistenceContext
    private EntityManager em;
    private static final Logger logger = 
            Logger.getLogger("dukestutoring.ejb.AdminBean");
    private CriteriaBuilder cb;
    private String username;

    @PostConstruct
    private void init() {
        cb = em.getCriteriaBuilder();
    }

    public Student createStudent(String firstName, String middleName,
            String lastName, String nickname, String suffix, String school,
            int grade, String email, String homePhone, String mobilePhone) {

        logger.log(Level.INFO,
                "AdminBean.createStudent(10 args): Persisting new student.");

        Student student = new Student();

        student.setFirstName(firstName);
        student.setMiddleName(middleName);
        student.setLastName(lastName);
        student.setNickname(nickname);
        student.setSuffix(suffix);
        student.setSchool(school);
        student.setGrade(grade);
        student.setEmail(email);
        student.setHomePhone(homePhone);
        student.setMobilePhone(mobilePhone);

        em.persist(student);

        return student;
    }

    public String createStudent(Student student) {
        logger.log(Level.INFO,
                "AdminBean.createStudent(1 arg): Persisting new student.");
        em.persist(student);
        return "createdStudent";
    }

    public void createStudents(List<Student> students) {
        for (Student s : students) {
            this.createStudent(s);
        }
    }

    public String editStudent(Student student) {
        logger.log(Level.INFO, "AdminBean.editStudent: Editing student.");
        em.merge(student);
        return "editedStudent";
    }

    public String editStudents(List<Student> students) {
        for (Student s : students) {
            this.editStudent(s);
        }
        return "editedStudents";
    }

    public String removeStudent(Student student) {
        student.setActive(false);
        em.merge(student);
        return "removedStudent";
    }

    public String removeStudents(List<Student> students) {
        for (Student s : students) {
            this.removeStudent(s);
        }
        return "removedStudents";
    }

    public String createGuardian(Guardian guardian, Student student) {
        logger.log(Level.INFO, "Creating guardian {0} for {1}",
                new Object[]{guardian.getName(), student.getName()});
        student.getGuardians().add(guardian);
        guardian.getStudents().add(student);
        em.merge(student);
        em.persist(guardian);
        return "createdGuardian";
    }

    public Guardian createGuardian(String firstName, String middleName,
            String lastName, String nickname, String suffix, String email,
            String homePhone, String mobilePhone, Student student) {
        logger.log(Level.INFO,
                "AdminBean.createGuardian(9 args): Persisting new guardian.");

        Guardian guardian = new Guardian();

        guardian.setFirstName(firstName);
        guardian.setMiddleName(middleName);
        guardian.setLastName(lastName);
        guardian.setNickname(nickname);
        guardian.setSuffix(suffix);
        guardian.setEmail(email);
        guardian.setHomePhone(homePhone);
        guardian.setMobilePhone(mobilePhone);
        student.getGuardians().add(guardian);
        guardian.getStudents().add(student);
        em.merge(student);
        em.persist(guardian);

        return guardian;
    }

    public String createGuardianWithList(Guardian guardian, List<Student> students) {
        for (Student s : students) {
            s.getGuardians().add(guardian);
            guardian.getStudents().add(s);
            em.merge(s);
        }
        em.persist(guardian);
        return "createdGuardian";
    }

    public String editGuardian(Guardian guardian) {
        em.merge(guardian);
        return "editedGuardian";
    }

    public String editGuardians(List<Guardian> guardians) {
        for (Guardian g : guardians) {
            this.editGuardian(g);
        }
        return "editedGuardians";
    }

    public String removeGuardianFromStudent(Guardian guardian, Student student) {
        if (guardian != null && student != null) {
            student.getGuardians().remove(guardian);
            guardian.getStudents().remove(student);
            em.merge(guardian);
            em.merge(student);
        }
        return "editedGuardian";
    }

    public String removeGuardian(Guardian guardian) {
        guardian.setActive(false);
        List<Student> students = guardian.getStudents();
        for (Student s : students) {
            s.getGuardians().remove(guardian);
            em.merge(s);
        }
        em.merge(guardian);
        return "removedGuardian";
    }

    public String removeGuardians(List<Guardian> guardians) {
        for (Guardian g : guardians) {
            this.removeGuardian(g);
        }
        return "removedGuardians";
    }

    public String addGuardiansToStudent(List<Guardian> guardians, Student student) {
        for (Guardian g : guardians) {
            student.getGuardians().add(g);
            g.getStudents().add(student);
            em.merge(g);
        }
        em.merge(student);
        return "addedGuardians";
    }

    public String createAddress(Address address, Person person) {
        person.getAddresses().add(address);
        address.setPerson(person);
        em.merge(person);
        em.persist(address);
        return "createdAddress";
    }

    public Address createAddress(String street1, String street2, String city,
            String province, String country, String postalCode, Boolean isPrimary,
            Student student) {

        Address address = new Address();
        address.setStreet1(street1);
        address.setStreet2(street2);
        address.setCity(city);
        address.setProvince(province);
        address.setCountry(country);
        address.setPostalCode(postalCode);
        address.setIsPrimary(isPrimary);
        address.setStreet1(street1);
        address.setStreet1(street1);
        address.setPerson(student);
        student.getAddresses().add(address);
        address.setPerson(student);
        em.merge(student);
        em.persist(address);
        
        return address;
    }

    public String editAddress(Address address) {
        em.merge(address);
        return "editedAddress";
    }

    public String editAddresses(List<Address> addresses) {
        for (Address a : addresses) {
            this.editAddress(a);
        }
        return "editedAddresses";
    }

    public String removeAddress(Address address) {
        address.setActive(false);
        Person person = address.getPerson();
        person.getAddresses().remove(address);
        em.merge(person);
        em.merge(address);
        return "removedAddress";
    }

    public String removeAddresses(List<Address> addresses) {
        for (Address a : addresses) {
            this.removeAddress(a);
        }
        return "removedAddresses";
    }

    public List<Guardian> getAllGuardians() {
        CriteriaQuery<Guardian> cq = em.getCriteriaBuilder().createQuery(Guardian.class);
        Root<Guardian> guardian = cq.from(Guardian.class);
        cq.select(guardian);
        cq.distinct(true);
        TypedQuery<Guardian> q = em.createQuery(cq);
        return q.getResultList();
    }

    public List<Address> getAllAddresses() {
        CriteriaQuery<Address> cq = em.getCriteriaBuilder().createQuery(Address.class);
        Root<Address> address = cq.from(Address.class);
        cq.select(address);
        cq.where(cb.isTrue(address.get(Address_.active)));
        cq.distinct(true);
        TypedQuery<Address> q = em.createQuery(cq);
        return q.getResultList();
    }

    public List<Student> getAllInactiveStudents() {
        CriteriaQuery<Student> cq = em.getCriteriaBuilder().createQuery(Student.class);
        Root<Student> student = cq.from(Student.class);
        cq.select(student);
        cq.where(cb.isFalse(student.get(Student_.active)));
        cq.distinct(true);
        TypedQuery<Student> q = em.createQuery(cq);
        return q.getResultList();
    }

    public String activateStudent(Student student) {
        student.setActive(true);
        em.merge(student);
        return "activatedStudent";
    }

    public Student findStudentById(Long id) {
        logger.log(Level.INFO, "Finding student with ID: {0}", id);
        CriteriaQuery<Student> cq = em.getCriteriaBuilder().createQuery(Student.class);
        Root<Student> student = cq.from(Student.class);
        cq.select(student);
        cq.where(cb.isTrue(student.get(Student_.active)));
        cq.where(cb.equal(student.get(Student_.id), id));
        cq.distinct(true);
        TypedQuery<Student> q = em.createQuery(cq);
        return q.getSingleResult();
    }

    public Guardian findGuardianById(Long id) {
        logger.log(Level.INFO, "Finding Guardian with ID: {0}", id);
        return (Guardian) em.find(Guardian.class, id);
    }

    public String createAdministrator(Administrator admin) {
        em.persist(admin);
        return "createdAdministrator";
    }

    public String getUsername() {
        return FacesContext.getCurrentInstance().getExternalContext()
                .getUserPrincipal().getName();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLoggedIn() {
        return FacesContext.getCurrentInstance().getExternalContext()
                .isUserInRole("Administrator");
    }

    public String logout() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.invalidateSession();
        return "../index.xhtml?faces-redirect=true";
    }
}
