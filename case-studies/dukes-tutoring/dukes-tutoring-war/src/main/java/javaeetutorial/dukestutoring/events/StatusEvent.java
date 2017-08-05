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
package javaeetutorial.dukestutoring.events;

import java.io.Serializable;
import javaeetutorial.dukestutoring.entity.Student;

/**
 *
 * @author ievans
 */
public class StatusEvent implements Serializable {
    
    private Student student;

    public StatusEvent() {
    }
    
    public StatusEvent(Student s) {
        student = s;
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
    
}
