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

package javaeetutorial.batch.webserverlog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import javax.batch.api.Batchlet;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

/* Batchlet artifact that counts the number of purchase page views
 * based on the filtered items. */
@Dependent
@Named("MobileBatchlet")
public class MobileBatchlet implements Batchlet {
    private BufferedReader breader;
    private String fileName;
    private String buyPage;
    private String fileOutName;
    private int totalVisits = 0;
    private int pageVisits = 0;
    @Inject
    JobContext jobCtx;
    
    public MobileBatchlet() { }
    
    /* What percentage of mobile or table users buy products? */
    @Override
    public String process() throws Exception {
        /* Get properties from the job definition file */
        fileName = jobCtx.getProperties().getProperty("filtered_file_name");
        buyPage = jobCtx.getProperties().getProperty("buy_page");
        fileOutName = jobCtx.getProperties().getProperty("out_file_name");
        
        /* Count from the output of the previous chunk step */
        breader = new BufferedReader(new FileReader(fileName));
        String line = breader.readLine();
        while (line != null) {
            String[] lineSplit = line.split(", ");
            if (buyPage.compareTo(lineSplit[1]) == 0)
                pageVisits++;
            totalVisits++;
            line = breader.readLine();
        }
        breader.close();
        
        /* Write the result */
        try (BufferedWriter bwriter = 
               new BufferedWriter(new FileWriter(fileOutName, false))) {
            double percent = 100.0 * (1.0 * pageVisits) / (1.0 * totalVisits);
            bwriter.write(String.format("%d, %d, %.02f", pageVisits,
                                                         totalVisits, percent));
        }
        return "COMPLETED";
    }

    @Override
    public void stop() throws Exception {
        breader.close();
    }
    
}
