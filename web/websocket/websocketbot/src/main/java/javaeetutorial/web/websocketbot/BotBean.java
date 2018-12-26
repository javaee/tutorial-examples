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
