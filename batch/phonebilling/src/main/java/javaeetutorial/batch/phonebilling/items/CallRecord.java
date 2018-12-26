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
