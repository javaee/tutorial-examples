/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.websimplemessage;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSRuntimeException;
import javax.jms.Queue;

@Named
@RequestScoped
public class ReceiverBean {

    static final Logger logger = Logger.getLogger("ReceiverBean");
    @Inject
    private JMSContext context;
    @Resource(lookup = "java:comp/jms/webappQueue")
    private Queue queue;

    /**
     * Creates a new instance of ReceiverBean
     */
    public ReceiverBean() {
    }

    /*
     * Within a try-with-resources block, create context.
     * Create consumer, then receive message, using a timeout.
     * Create FacesMessage to display on page.
     */
    public void getMessage() {
        try {
            JMSConsumer receiver = context.createConsumer(queue);
            String text = receiver.receiveBody(String.class, 1000);

            if (text != null) {
                FacesMessage facesMessage =
                        new FacesMessage("Reading message: " + text);
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            } else {
                FacesMessage facesMessage =
                        new FacesMessage("No message received after 1 second");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            }
        } catch (JMSRuntimeException t) {
            logger.log(Level.SEVERE,
                    "ReceiverBean.getMessage: Exception: {0}",
                    t.toString());
        }
    }
}
