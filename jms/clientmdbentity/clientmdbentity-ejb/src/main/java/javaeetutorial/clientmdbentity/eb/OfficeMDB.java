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

package javaeetutorial.clientmdbentity.eb;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * The OfficeMDB class is a message-driven bean. It implements the
 * javax.jms.MessageListener interface. It is defined as public (but not final
 * or abstract).
 */
/* At present, must use mappedName if destination is defined elsewhere than
 * in MDB (GlassFish issue 20715).
 */
@MessageDriven(mappedName="java:app/jms/HRTopic", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType",
            propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "subscriptionDurability",
            propertyValue = "Durable"),
    @ActivationConfigProperty(propertyName = "clientId",
            propertyValue = "OfficeMDB"),
    @ActivationConfigProperty(propertyName = "subscriptionName",
            propertyValue = "OfficeMDB")
})
public class OfficeMDB implements MessageListener {

    static final Logger logger = Logger.getLogger("OfficeMDB");
    private final Random processingTime = new Random();
    @Resource
    public MessageDrivenContext mdc;
    @PersistenceContext
    EntityManager em;
    @Inject
    private JMSContext context;

    /**
     * Constructor, which is public and takes no arguments.
     */
    public OfficeMDB() {
    }

    /**
     * onMessage method, declared as public (but not final or static), with a
     * return type of void, and with one argument of type javax.jms.Message.
     *
     * Casts the incoming Message to a MapMessage, retrieves its contents, and
     * assigns the new hire to an office. Calls the compose method to store the
     * information in the persistence entity and, if work is complete, to send a
     * reply message to the client.
     *
     * @param inMessage the incoming message
     */
    @Override
    public void onMessage(Message inMessage) {
        MapMessage msg;
        String key;
        String name;
        String position;
        int officeNumber;

        try {
            if (inMessage instanceof MapMessage) {
                msg = (MapMessage) inMessage;
                key = msg.getString("HireID");
                name = msg.getString("Name");
                position = msg.getString("Position");
                logger.log(Level.INFO, "OfficeMDB.onMessage:"
                        + " Message received for employeeId {0}", key);

                officeNumber = new Random().nextInt(300) + 1;

                /* Simulate processing time taking 1 to 10
                 * seconds.
                 */
                Thread.sleep(processingTime.nextInt(10) * 1000);
                compose(key, name, officeNumber, msg);
            } else {
                logger.log(Level.WARNING,
                        "OfficeMDB.onMessage: Message of wrong type: {0}",
                        inMessage.getClass().getName());
            }
        } catch (JMSException | InterruptedException e) {
            logger.log(Level.SEVERE,
                    "OfficeMDB.onMessage: Exception: {0}", e.toString());
            mdc.setRollbackOnly();
        }
    }

    /**
     * compose method, helper to onMessage method.
     *
     * Locates the row of the database represented by the primary key and adds
     * the office number allocated for the new hire.
     *
     * @param key employee ID, primary key
     * @param name employee name
     * @param officeNumber office number
     * @param msg the message received
     */
    void compose(String key, String name, int officeNumber, Message msg) {
        SetupOffice so = null;
        MapMessage replyMsg;
        Destination replyDest;
        String replyCorrelationMsgId;
        boolean done = false;

        try {
            so = em.find(SetupOffice.class, key);
            if (so != null) {
                logger.log(Level.INFO, "OfficeMDB.compose: "
                        + "Found join entity for employeeId {0}", key);
            }
        } catch (IllegalArgumentException iae) {
            logger.log(Level.WARNING,
                    "EquipmentMDB.compose: No join entity found: {0}",
                    iae.toString());
        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "EquipmentMDB.compose: em.find failed without"
                    + " throwing IllegalArgumentException: {0}", e.toString());
        }

        // No entity found; create it
        if (so == null) {
            try {
                logger.log(Level.INFO, "OfficeMDB.compose: "
                        + "Creating join entity for employeeId {0}", key);
                so = new SetupOffice(key, name);
                em.persist(so);
            } catch (Exception e) {
                logger.log(Level.WARNING,
                        "EquipmentMDB.compose: Could not create join entity: {0}",
                        e.toString());
                mdc.setRollbackOnly();
            }
        }

        // Entity found or created, so add office
        if (so != null) {
            try {
                done = so.doOfficeNumber(officeNumber);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "OfficeMDB.compose: "
                        + "Could not get office for employeeId {0}", key);
                mdc.setRollbackOnly();
            }
        }

        /* Whichever bean receives the information that the setup is
         * complete sends a message back to the client and removes
         * the entity. Context uses a transacted session by default.
         */
        if (done) {
            try {
                /*
                 * Send reply to messages aggregated by this
                 * composite entity.  Call createReplyMsg
                 * to construct the reply.
                 */
                replyDest = msg.getJMSReplyTo();
                replyCorrelationMsgId = msg.getJMSMessageID();
                replyMsg = createReplyMsg(so, context, replyCorrelationMsgId);
                context.createProducer().send(replyDest, replyMsg);
                logger.log(Level.INFO, "OfficeMDB.compose: "
                        + "Sent reply message for employeeId {0}",
                        so.getEmployeeId());
            } catch (JMSException je) {
                logger.log(Level.SEVERE, "OfficeMDB.compose: "
                        + "JMSException: {0}", je.toString());
            }

            if (so != null) {
                logger.log(Level.INFO, "OfficeMDB.compose: "
                        + "REMOVING SetupOffice entity employeeId={0}, Name={1}",
                        new Object[]{so.getEmployeeId(), so.getEmployeeName()});
                em.remove(so);
            }
        }
    }

    /**
     * The createReplyMsg method composes the reply message with the new hire
     * information.
     *
     * @param context the JMSContext object for the message producer
     * @param msgId	the reply correlation message ID
     *
     * @return a MapMessage containing the reply message
     */
    private MapMessage createReplyMsg(SetupOffice so, JMSContext context,
            String msgId)
            throws JMSException {

        MapMessage replyMsg = context.createMapMessage();
        replyMsg.setString("employeeId", so.getEmployeeId());
        replyMsg.setString("employeeName", so.getEmployeeName());
        replyMsg.setString("equipmentList", so.getEquipmentList());
        replyMsg.setInt("officeNumber", so.getOfficeNumber());
        replyMsg.setJMSCorrelationID(msgId);

        return replyMsg;
    }
}
