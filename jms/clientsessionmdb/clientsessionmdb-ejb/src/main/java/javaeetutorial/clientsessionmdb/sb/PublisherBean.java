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

package javaeetutorial.clientsessionmdb.sb;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.jms.Topic;

/**
 * Bean class for Publisher enterprise bean. Defines publishNews business method
 * as well as required methods for a stateless session bean.
 */
@Stateless
@Remote({
    PublisherRemote.class
})
public class PublisherBean implements PublisherRemote {

    @Resource
    private SessionContext sc;
    @Resource(lookup = "java:module/jms/newsTopic")
    private Topic topic;
    @Inject
    private JMSContext context;
    static final String[] messageTypes = {
        "Nation/World", "Metro/Region", "Business", "Sports", "Living/Arts",
        "Opinion"
    };
    static final Logger logger = Logger.getLogger("PublisherBean");

    public PublisherBean() {
    }

    /**
     * Chooses a message type by using the random number generator found in
     * java.util. Called by publishNews().
     *
     * @return the String representing the message type
     */
    private String chooseType() {
        int whichMsg;
        Random rgen = new Random();

        whichMsg = rgen.nextInt(messageTypes.length);

        return messageTypes[whichMsg];
    }

    /**
     * Creates producer and message. Sends messages after setting their NewsType
     * property and using the property value as the message text. Messages are
     * received by MessageBean, a message-driven bean that uses a message
     * selector to retrieve messages whose NewsType property has certain values.
     */
    @Override
    public void publishNews() {
        TextMessage message;
        int numMsgs = messageTypes.length * 3;
        String messageType;

        try {
            message = context.createTextMessage();

            for (int i = 0; i < numMsgs; i++) {
                messageType = chooseType();
                message.setStringProperty("NewsType", messageType);
                message.setText("Item " + i + ": " + messageType);
                logger.log(Level.INFO,
                        "PUBLISHER: Setting message text to: {0}",
                        message.getText());
                context.createProducer().send(topic, message);
            }
        } catch (JMSException t) {
            logger.log(Level.SEVERE,
                    "PublisherBean.publishNews: Exception: {0}", t.toString());
            sc.setRollbackOnly();
        }
    }
}
