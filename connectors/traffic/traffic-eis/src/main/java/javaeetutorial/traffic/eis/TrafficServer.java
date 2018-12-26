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

package javaeetutorial.traffic.eis;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TrafficServer {
    
    private static List<Socket> clients;
    
    public static void main(String[] args) throws IOException {
        
        clients = new ArrayList<>();
        final ServerSocket server = new ServerSocket(4008);
        System.out.println("Traffic EIS accepting connections on port 4008");
        
        /* Accept connections */
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket client = server.accept();
                        synchronized (TrafficServer.class) {
                            clients.add(client);
                        }
                        System.out.println("Client connected");
                    } catch (IOException e) { }
                }
            }
        }).start();
        
        /* Send traffic information to all connected peers */
        PrintWriter out;
        String report;
        TrafficService tsvc = new TrafficService();
        while (true) {
            report = tsvc.getReport();
            for (Socket client : clients) {
                out = new PrintWriter(client.getOutputStream(), true);
                //System.out.println(report);
                out.println(report);
            }
            
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) { }
        }
    }

}
