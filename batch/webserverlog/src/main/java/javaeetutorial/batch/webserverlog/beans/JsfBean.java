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

package javaeetutorial.batch.webserverlog.beans;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/* Handles data for the Facelets pages and starts the batch job */
@Named
@SessionScoped
public class JsfBean implements Serializable {
    
    private long execID;
    private JobOperator jobOperator;
    private static final Logger logger = Logger.getLogger("JsfBean");
    private static final long serialVersionUID = 3712686178567411830L;
    
    /* Show to the user the log file that the batch job processes */
    public String getInputLog() {
        String inputLog = "";
        
        /* Read from the log file included with the application
         * (webserverlog/WEB-INF/classes/log1.txt) */
        BufferedReader breader;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream("log1.txt");
        breader = new BufferedReader(new InputStreamReader(iStream));
        
        try {
            String line = breader.readLine();
            while (line != null) {
                inputLog += line + '\n';
                line = breader.readLine();
            }
        } catch (IOException ex) {
            logger.log(Level.INFO, ex.toString());
        }
        return inputLog;
    }
    
    /* --JSF navigation method--
     * Starts the batch job and navigates to the next page */
    public String startBatchJob() {
        jobOperator = BatchRuntime.getJobOperator();
        execID = jobOperator.start("webserverlog", null);
        return "jobstarted";
    }
    
    /* Show the current status of the job */
    public String getJobStatus() {
        return jobOperator.getJobExecution(execID).getBatchStatus().toString();
    }
    
    public boolean isCompleted() {
        return (getJobStatus().compareTo("COMPLETED") == 0);
    }
    
    /* Show the results */
    public String showResults() throws IOException {
        if (isCompleted()) {
            String returnStr;
            /* open file name for output, split comas */
            BufferedReader breader;
            breader = new BufferedReader(new FileReader("result1.txt"));
            String[] results = breader.readLine().split(", ");
            /* create output string */
            returnStr = String.format("%s purchases of %s tablet page views, (%s percent)", 
                         results[0], results[1], results[2]);
            return returnStr;
        } else {
            return "";
        }
    }
}
