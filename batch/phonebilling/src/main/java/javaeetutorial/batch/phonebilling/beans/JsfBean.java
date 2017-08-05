/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.batch.phonebilling.beans;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javaeetutorial.batch.phonebilling.items.PhoneBill;
import javaeetutorial.batch.phonebilling.tools.CallRecordLogCreator;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/* Managed bean for the JSF front end pages.
 * - Shows the log file to the user.
 * - Enables the user to submit the job.
 * - Checks on the status of the job.
 * - Shows the results on a JSF page.
 */
@Named
@SessionScoped
public class JsfBean implements Serializable {
    
    CallRecordLogCreator logtool;
    private long execID;
    private JobOperator jobOperator;
    @PersistenceContext
    EntityManager em;
    private static final Logger logger = Logger.getLogger("JsfBean");
    private static final long serialVersionUID = 6775054787257816151L;
    
    /* Create a long log file of calls */
    public String createAndShowLog() throws FileNotFoundException, IOException {
        
        String log = "";
        BufferedReader breader;
        
        logtool = new CallRecordLogCreator();
        logtool.writeToFile("log1.txt");
        breader = new BufferedReader(new FileReader("log1.txt"));
        String line = breader.readLine();
        while (line != null) {
            log += line + '\n';
            line = breader.readLine();
        }
        return log;
    }
    
    /* Submit the batch job to the batch runtime.
     * JSF Navigation method (return the name of the next page) */
    public String startBatchJob() {
        jobOperator = BatchRuntime.getJobOperator();
        execID = jobOperator.start("phonebilling", null);
        return "jobstarted";
    }
    
    /* Get the status of the job from the batch runtime */
    public String getJobStatus() {
        return jobOperator.getJobExecution(execID).getBatchStatus().toString();
    }
    
    public boolean isCompleted() {
        return (getJobStatus().compareTo("COMPLETED") == 0);
    }
    
    /* Because we can't output HTML code to a JSF page safely,
     * we provide a list of bills. Each bill itself is a list of lines of text.
     * This is for easy representation in JSF tables.
     */
    public List<List<String>> getRowList() throws FileNotFoundException, 
                                                  IOException {
        List<List<String>> rowList = new ArrayList<>();
        
        if (isCompleted()) {
            String query = "SELECT b FROM PhoneBill b ORDER BY b.phoneNumber";
            Query q = em.createQuery(query);
            
            for (Object billObject : q.getResultList()) {
                /* Each bill */
                PhoneBill bill = (PhoneBill) billObject;
                List<String> lines = new ArrayList<>();
                
                FileReader reader = new FileReader(bill.getPhoneNumber()+".txt");
                try (BufferedReader breader = new BufferedReader(reader)) {
                    String line = breader.readLine();
                    while (line != null) {
                        /* Each call in a bill */
                        lines.add(line);
                        line = breader.readLine();
                    }
                }
                System.out.println(lines);
                rowList.add(lines);
            }
        }
        return rowList;
    }
}
