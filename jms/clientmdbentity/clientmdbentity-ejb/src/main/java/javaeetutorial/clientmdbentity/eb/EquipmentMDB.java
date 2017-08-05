/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
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
 * The EquipmentMDB class is a message-driven bean. It implements the
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
            propertyValue = "EquipmentMDB"),
    @ActivationConfigProperty(propertyName = "subscriptionName",
            propertyValue = "EquipmentMDB")
})
public class EquipmentMDB implements MessageListener {

    static final Logger logger = Logger.getLogger("EquipmentMDB");
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
    public EquipmentMDB() {
    }

    /**
     * onMessage method, declared as public (but not final or static), with a
     * return type of void, and with one argument of type javax.jms.Message.
     *
     * Casts the incoming Message to a MapMessage, retrieves its contents, and
     * assigns equipment appropriate to the new hire's position. Calls the
     * compose method to store the information in the persistence entity and, if
     * work is complete, to send a reply message to the client.
     *
     * @param inMessage the incoming message
     */
    @Override
    public void onMessage(Message inMessage) {
        MapMessage msg;
        String key;
        String name;
        String position;
        String equipmentList;

        try {
            if (inMessage instanceof MapMessage) {
                msg = (MapMessage) inMessage;
                key = msg.getString("HireID");
                name = msg.getString("Name");
                position = msg.getString("Position");
                logger.log(Level.INFO, "EquipmentMDB.onMessage: "
                        + "Message received for employeeId {0}", key);
                switch (position) {
                    case "Programmer":
                        equipmentList = "Desktop System";
                        break;
                    case "Senior Programmer":
                        equipmentList = "Laptop";
                        break;
                    case "Manager":
                        equipmentList = "Tablet";
                        break;
                    case "Director":
                        equipmentList = "Mobile Phone";
                        break;
                    default:
                        equipmentList = "Baton";
                        break;
                }

                /* Simulate processing time taking 1 to 10
                 * seconds.
                 */
                Thread.sleep(processingTime.nextInt(10) * 1000);
                compose(key, name, equipmentList, msg);
            } else {
                logger.log(Level.WARNING,
                        "EquipmentMDB.onMessage: Message of wrong type: {0}",
                        inMessage.getClass().getName());
            }
        } catch (JMSException | InterruptedException e) {
            logger.log(Level.SEVERE,
                    "EquipmentMDB.onMessage: Exception: {0}", e.toString());
            mdc.setRollbackOnly();
        }
    }

    /**
     * compose method, helper to onMessage method.
     *
     * Locates the row of the database represented by the primary key and adds
     * the equipment allocated for the new hire.
     *
     * @param key employee ID, primary key
     * @param name employee name
     * @param equipmentList equipment allocated based on position
     * @param msg the message received
     */
    void compose(String key, String name, String equipmentList, Message msg) {
        SetupOffice so = null;
        MapMessage replyMsg;
        Destination replyDest;
        String replyCorrelationMsgId;
        boolean done = false;

        try {
            so = em.find(SetupOffice.class, key);
            if (so != null) {
                logger.log(Level.INFO, "EquipmentMDB.compose: "
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
                logger.log(Level.INFO, "EquipmentMDB.compose: "
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

        // Entity found or created, so add equipment
        if (so != null) {
            try {
                done = so.doEquipmentList(equipmentList);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "EquipmentMDB.compose: "
                        + "Could not get equipment for employeeId {0}", key);
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
                logger.log(Level.INFO, "EquipmentMDB.compose: "
                        + "Sent reply message for employeeId {0}",
                        so.getEmployeeId());
            } catch (JMSException je) {
                logger.log(Level.SEVERE, "EquipmentMDB.compose: "
                        + "JMSException: {0}", je.toString());
            }

            if (so != null) {
                logger.log(Level.INFO, "EquipmentMDB.compose: "
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
