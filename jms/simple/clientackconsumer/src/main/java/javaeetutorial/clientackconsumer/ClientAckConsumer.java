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

package javaeetutorial.clientackconsumer;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;

public class ClientAckConsumer {

    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(lookup = "jms/MyQueue")
    private static Queue queue;

    public static void main(String[] args) {
        JMSConsumer consumer;

        /*
         * Create client-acknowledge context for receiver.
         * Receive message and process it.
         * Acknowledge message.
         */
        try (JMSContext context =
                connectionFactory.createContext(JMSContext.CLIENT_ACKNOWLEDGE);) {
            System.out.println(
                    "Created client-acknowledge JMSContext");
            consumer = context.createConsumer(queue);

            while (true) {
                Message m = consumer.receive(1000);

                if (m != null) {
                    if (m instanceof TextMessage) {
                        // Comment out the following two lines to receive
                        // a large volume of messages
                        System.out.println(
                                "Reading message: " + m.getBody(String.class));
                        System.out.println(
                                "Acknowledging TextMessage");
                        context.acknowledge();
                    } else {
                        System.out.println(
                                "Acknowledging non-text control message");
                        context.acknowledge();
                        break;
                    }
                }
            }
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
        }
    }
}
