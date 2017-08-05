/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.taskcreator;

import java.io.Serializable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/* Bean for the index.xhtml JSF page */
@Named
@SessionScoped
public class TaskCreatorBean implements Serializable {
    
    private static final long serialVersionUID = 7393500655950989708L;
    private static final Logger log = Logger.getLogger("TaskCreatorBean");
    
    @EJB
    TaskEJB taskEJB;
    
    private String taskMessages;
    private String taskType;
    private String taskName;
    private String periodicTask;

    public TaskCreatorBean() {
        taskMessages = "";
        taskType = "IMMEDIATE";
        taskName = "";
        periodicTask = "";
    }
    
    public void setTaskMessages(String msg) {
        taskMessages = msg;
    }
    public String getTaskMessages() {
        taskMessages = taskEJB.getInfoField();
        return taskMessages;
    }
    
    public void setTaskType(String t) {
        taskType = t;
    }
    public String getTaskType() {
        return taskType;
    }
    
    public void setTaskName(String n) {
        taskName = n;
    }
    public String getTaskName() {
        return taskName;
    }
    
    public void setPeriodicTask(String t) {
        periodicTask = t;
    }
    public String getPeriodicTask() {
        return periodicTask;
    }
    
    public Set<String> getPeriodicTasks() {
        return taskEJB.getPeriodicTasks();
    }
    
    /* Executed via an AJAX call in the Submit button */
    public void submitTask() {
        if (taskEJB.getPeriodicTasks().contains(taskName) == false) {
            /* Create a new task object */
            Task task = new Task(taskName, taskType);
            /* Use the managed executor objects to run the task */
            taskEJB.submitTask(task, taskType);
            taskType = "IMMEDIATE";
            taskName = "";
        }
    }
    
    /* Executed via an AJAX call in the Cancel Task button */
    public void cancelTask() {
        log.log(Level.INFO, "[TaskCreatorBean] Cancelling task {0}", periodicTask);
        taskEJB.cancelPeriodicTask(periodicTask);
    }
    
    /* Executed via an AJAX call in the Clean Log button */
    public void clearInfoField() {
        taskEJB.clearInfoField();
    }

}
