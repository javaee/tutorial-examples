/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.trading.eis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TradeExecServer implements Runnable {
    
    private Socket client;
    
    public static void main(String[] args) throws IOException {
        
        ServerSocket server = new ServerSocket(4004);
        System.out.println("Trade execution server listening on port 4004.");
        
        while (true) {
            Socket client = server.accept();
            Thread sthread = new Thread(new TradeExecServer(client));
            sthread.start();
        }
    }
    
    public TradeExecServer(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        String inline;
        String outline;
        try {
            TradeProcessor tradeproc = new TradeProcessor();
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            InputStreamReader isr = new InputStreamReader(client.getInputStream());
            BufferedReader in = new BufferedReader(isr);
            
            System.out.println("Client connected.");
            out.println(tradeproc.getGreeting());
            out.println(tradeproc.getReady());
            
            while ((inline = in.readLine()) != null) {
                System.out.println("Received: " + inline);
                outline = tradeproc.processCommand(inline);
                System.out.println("Sent: " + outline);
                out.println(outline);
                if (outline.compareTo("BYE Closing connection.") == 0) {
                    break;
                }
            }
            client.close();
        } catch (IOException ex) { }
    }
}
