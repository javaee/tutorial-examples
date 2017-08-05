/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
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
