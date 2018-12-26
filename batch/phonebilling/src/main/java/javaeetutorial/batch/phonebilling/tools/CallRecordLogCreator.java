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

package javaeetutorial.batch.phonebilling.tools;

import java.util.Calendar;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/* This class creates simulated call logs for the batch application.
 * This is just a supporting class.
 * 
 * Log entries look like this:
 * {"datetime":"03/01/2013 04:03","from":"555-0109",
 * "to":"555-0112","length":"05:39"}
 */
public class CallRecordLogCreator {
    
    private Calendar cal;
    private Random rnd;
    private String[] srcNumbers;
    private String[] dstNumbers;
    private String[] records;
    private static final Logger logger = Logger.getLogger("CalRecordLogCreator");
    
    public CallRecordLogCreator() {
        this(10, 1000);
    }
    
    public CallRecordLogCreator(int nSrcNumbers, int nCalls) {
        rnd = new Random();
        srcNumbers = new String[nSrcNumbers];
        dstNumbers = new String[nSrcNumbers];
        records = new String[nCalls];
        
        nSrcNumbers = Math.min(nSrcNumbers, 49);
        for (int i=0; i<nSrcNumbers; i++) {
            srcNumbers[i] = String.format("555-01%02d", i+1);
            dstNumbers[i] = String.format("555-01%02d", i+1+nSrcNumbers);
        }
        
        cal = Calendar.getInstance();
        cal.set(2013, 2, 1, 04, 01, 00);
        
        for (int i=0; i<nCalls; i++)
            records[i] = this.generateRecord();
    }
    
    private String generateRecord() {
        cal.add(Calendar.SECOND, rnd.nextInt(20) + 1);
        cal.add(Calendar.MINUTE, rnd.nextInt(10) + 1);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String datetime = sdf.format(cal.getTime());
        String from = srcNumbers[rnd.nextInt(srcNumbers.length)];
        String to = dstNumbers[rnd.nextInt(dstNumbers.length)];
        String length = String.format("%02d:%02d", rnd.nextInt(7), rnd.nextInt(60));
        return String.format("{\"datetime\":\"%s\",\"from\":\"%s\",\"to\":\"%s\",\"length\":\"%s\"}", 
                             datetime, from, to, length);
    }
    
    public void writeToFile(String fileName) {
        BufferedWriter bwriter;
        try {
            bwriter = new BufferedWriter(new FileWriter(fileName));
            for (int i=0; i<records.length; i++) {
                bwriter.write(records[i]);
                bwriter.newLine();
            }
            bwriter.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, e.toString());
        }
    }
    
    /*public static void main(String args[]) {
        CallRecordLogCreator c = new CallRecordLogCreator();
        for (int i=0; i<100; i++)
            System.out.println(c.generateRecord());
        c.writeToFile("C:/log2.txt");
    }*/
}
