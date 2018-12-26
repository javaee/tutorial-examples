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

package javaeetutorial.batch.webserverlog.tools;

import java.util.Calendar;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/* (This file is NOT required to test the sample)
 * - Uncomment the main method and run this class to generate
 * sample log files of arbitrary length to test the batch sample
 * with a larger file, if you wish to do so.
 * - Replace WEB-INF/classes/log1.txt with the new file. */
public class WebServerLogCreator {

    private Calendar cal;
    private String[] browsers;
    private String[] urls;
    private BufferedWriter bwriter;
    private static final Logger logger = Logger.getLogger("WebServerLogCreator");

    public WebServerLogCreator(String filename) {
        cal = Calendar.getInstance();
        cal.set(2013, 2, 1, 04, 01, 00);
        browsers = new String[5];
        browsers[0] = "Desktop Browser A";
        browsers[1] = "Desktop Browser B";
        browsers[2] = "Mobile Browser C";
        browsers[3] = "Tablet Browser D";
        browsers[4] = "Tablet Browser E";
        urls = new String[4];
        urls[0] = "/index.html";
        urls[1] = "/auth/login.html";
        urls[2] = "/auth/buy.html";
        urls[3] = "/auth/logout.html";
        try {
            bwriter = new BufferedWriter(new FileWriter(filename));
        } catch (IOException ex) {
            logger.log(Level.INFO, ex.toString());
        }
    }

    public String writeLogLine() {
        String line = "";
        Random rnd = new Random();
        cal.add(Calendar.SECOND, rnd.nextInt(20) + 1);
        cal.add(Calendar.MINUTE, rnd.nextInt(10) + 1);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String datetime = sdf.format(cal.getTime());
        String ipaddr = "" + 192
                + "." + 168
                + "." + (rnd.nextInt(254) + 1)
                + "." + (rnd.nextInt(254) + 1) + "";
        String browser = browsers[rnd.nextInt(5)];
        String url = urls[rnd.nextInt(4)];
        try {
            line = datetime + ", " + ipaddr + ", "
               + browser + ", " + url;
            bwriter.write(line);
            bwriter.newLine();
        } catch (IOException ex) {
            logger.log(Level.INFO, ex.toString());
        }
        return line;
    }

    public void close() {
        try {
            bwriter.flush();
            bwriter.close();
        } catch (IOException ex) {
            logger.log(Level.INFO, ex.toString());
        }
    }

    /*public static void main(String[] args) {
        WebServerLogCreator log = new WebServerLogCreator("C:/log1.txt");
        for (int i = 0; i < 500; i++) {
            log.writeLogLine();
        }
        log.close();
    }*/
}
