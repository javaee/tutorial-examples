/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.web.websocketbot;

import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.inject.Named;

@Named
public class BotBean {
    
    /* Respond to a message from the chat */
    public String respond(String msg) {
        String response;           
        
        /* Remove question marks */
        msg = msg.toLowerCase().replaceAll("\\?", "");
        if (msg.contains("how are you")) {
            response = "I'm doing great, thank you!";
        } else if (msg.contains("how old are you")) {
            Calendar dukesBirthday = new GregorianCalendar(1995, Calendar.MAY, 23);
            Calendar now = GregorianCalendar.getInstance();
            int dukesAge = now.get(Calendar.YEAR) - dukesBirthday.get(Calendar.YEAR);
            response = String.format("I'm %d years old.", dukesAge);
        } else if (msg.contains("when is your birthday")) {
            response = "My birthday is on May 23rd. Thanks for asking!";
        } else if (msg.contains("your favorite color")) {
            response = "My favorite color is blue. What's yours?";
        } else {
            response = "Sorry, I did not understand what you said. ";
            response += "You can ask me how I'm doing today; how old I am; or ";
            response += "what my favorite color is.";
        }
        try {
            Thread.sleep(1200);
        } catch (InterruptedException ex) { }
        return response;
    }
}
