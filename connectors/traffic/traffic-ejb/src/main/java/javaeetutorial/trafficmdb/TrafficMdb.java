/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.trafficmdb;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaeetutorial.traffic.rar.api.TrafficCommand;
import javaeetutorial.traffic.rar.api.TrafficListener;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.Topic;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;

/* Create a JMS destination to send filtered traffic messages */
@JMSDestinationDefinition(
    name = "java:app/traffic-ejb/traffictopic",
    interfaceName = "javax.jms.Topic",
    destinationName = "CityTopic"
)
/* Activate the traffic-rar resource adapter from this MDB */
@MessageDriven(
    activationConfig = { 
        @ActivationConfigProperty(
            propertyName = "port",
            propertyValue = "4008"
        )
    }
)
/* Receive messages from the EIS and filter them for "City1" only */
public class TrafficMdb implements TrafficListener {
    
    private static final String filterCity = "City1";
    private static final Logger log = Logger.getLogger("TrafficMdb");
    
    /* JMS objects to send filtered traffic updates */
    @Resource(lookup = "java:app/traffic-ejb/traffictopic")
    private Topic topic;
    @Inject
    private JMSContext jmsContext;
    
    public TrafficMdb() {
        log.info("[TrafficMdb] Constructor()");
    }
    
    /* The RA looks for methods annotated with @TrafficCommand */
    /* Processes "report" JSON messages from the traffic service */
    @TrafficCommand(name="report", info="Process report")
    public void processReport(String jsonReport) {
        
        String city, access, status;
        Map<String,String> accessStatusMap = new HashMap<>();
        
        log.info("[TrafficMdb] processReport()");
        //log.info(jsonReport);
        
        /* Parse the JSON traffic report message */
        JsonParser parser = Json.createParser(new StringReader(jsonReport));
        
        /* Ensure the message starts with {"report": [ ... */
        if (parser.hasNext() && parser.next() == JsonParser.Event.START_OBJECT &&
            parser.hasNext() && parser.next() == JsonParser.Event.KEY_NAME &&
            parser.getString().equals("report") &&
            parser.hasNext() && parser.next() == JsonParser.Event.START_ARRAY &&
            parser.hasNext()) {
            
            /* Parse array entries: 
             * {"city":"...", "access":"...", "status":"..."} */
            while (parser.hasNext()) {
                JsonParser.Event event = parser.next();
                if (event == JsonParser.Event.END_ARRAY)
                    break;
                else if (event == JsonParser.Event.START_OBJECT) {
                    /* For simplicty, assume each entry has the right format */
                    parser.next();
                    parser.next();
                    city = parser.getString();
                    parser.next();
                    parser.next();
                    access = parser.getString();
                    parser.next();
                    parser.next();
                    status = parser.getString();
                    /* Filter traffic messages for only one city */
                    if (city.compareTo(filterCity) == 0)
                        accessStatusMap.put(access, status);
                }
            }
            sendJMSUpdate(accessStatusMap);            
        } else
            log.info("[TrafficMdb] Wrong message format");
        
    }
    
    /* Send filtered traffic update to a JMS topic */
    private void sendJMSUpdate(Map<String,String> update) {
        
        StringWriter swriter = new StringWriter();
        try (JsonGenerator gen = Json.createGenerator(swriter)) {
            gen.writeStartObject();
            for (String access : update.keySet())
                gen.write(access, update.get(access));
            gen.writeEnd();
        }
        
        log.info("[TrafficMdb] sendJMSUpdate()");
        jmsContext.createProducer().send(topic, swriter.toString());
        log.log(Level.INFO, "[TrafficMdb] {0}", swriter.toString());
    }
    
}
