/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.batch.phonebilling.items;

import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/* This class is a Java Persistence API entity that
 * represents a phone call in the input log file.
 */
@Entity
public class CallRecord implements Serializable {
    
    @Id @GeneratedValue
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date datetime;
    private String fromNumber;
    private String toNumber;
    private int minutes;
    private int seconds;
    private BigDecimal price;
    private static final long serialVersionUID = -706813391935095052L;

    public CallRecord() {
    }
    
    public CallRecord(String datetime, String from, 
            String to, int min, int sec) 
            throws ParseException {
        /* Create a call record from its fields */
        SimpleDateFormat dformat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        this.datetime = dformat.parse(datetime);
        this.fromNumber = from;
        this.toNumber = to;
        this.minutes = min;
        this.seconds = sec;
    }
    
    public CallRecord(String jsonData) throws ParseException {
        
        /* Create a call record from a line of the log file (JSON) */
        String key = null;
        String value;
        HashMap<String,String> map = new HashMap<>();
        
        /* Parse entry into a map */
        JsonParser parser = Json.createParser(new StringReader(jsonData));
        while (parser.hasNext()) {
            switch(parser.next()) {
                case KEY_NAME:
                    key = parser.getString();
                    break;
                case VALUE_STRING:
                    value = parser.getString();
                    map.put(key, value);
                    break;
            }
        }
        
        /* Get a CallRecord from the map */
        SimpleDateFormat dformat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        datetime = dformat.parse(map.get("datetime"));
        fromNumber = map.get("from");
        toNumber = map.get("to");
        String[] length = map.get("length").split(":");
        minutes = Integer.parseInt(length[0]);
        seconds = Integer.parseInt(length[1]);
    }
    
    /* Getters and setters */
    public Date getDatetime() { return datetime; }
    public String getFromNumber() { return fromNumber; }
    public String getToNumber() { return toNumber; }
    public int getMinutes() { return minutes; }
    public int getSeconds() { return seconds; }
    public void setPrice(BigDecimal price) { 
        this.price = price.setScale(2, RoundingMode.HALF_EVEN);
    }
    public BigDecimal getPrice() { return price; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
