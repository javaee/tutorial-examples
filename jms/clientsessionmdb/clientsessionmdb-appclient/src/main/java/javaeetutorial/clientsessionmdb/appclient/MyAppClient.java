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

package javaeetutorial.clientsessionmdb.appclient;

import java.util.logging.Level;
import java.util.logging.Logger;
import javaeetutorial.clientsessionmdb.sb.PublisherRemote;
import javax.ejb.EJB;

/**
 * The MyAppClient class is the client program for this application. It calls
 * the publisher's publishNews method twice.
 */
public class MyAppClient {

    static final Logger logger = Logger.getLogger("MyAppClient");
    
    @EJB
    private static PublisherRemote publisher;

    public MyAppClient(String[] args) {
    }

    public static void main(String[] args) {
        MyAppClient client = new MyAppClient(args);
        client.doTest();
        System.exit(0);
    }

    public void doTest() {
        try {
            publisher.publishNews();
            publisher.publishNews();
            System.out.println("To view the bean output,");
            System.out.println(
                    " check <install_dir>/domains/domain1/logs/server.log.");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception:{0}", ex.toString());
        }
    }
}
