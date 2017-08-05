/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.jsonpstreaming;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.json.*;
import javax.json.stream.*;

/* This class manages the data from a JSF form, writes 
 * JSON data from it, and parses JSON data. */
@Named
@SessionScoped
public class StreamingBean implements Serializable {
    
    private static final long serialVersionUID = 5587157797666076243L;
    
    /* Form properties */
    protected static final String PHONE_TYPE_HOME = "Home";
    protected static final String PHONE_TYPE_MOBILE = "Mobile";
    protected String firstName = "Duke";
    protected String lastName = "Java";
    protected int age = 18;
    protected String streetAddress = "100 Internet Dr";
    protected String city = "JavaTown";
    protected String state = "JA";
    protected String postalCode = "12345";
    protected String phoneNumber1 = "111-111-1111";
    protected String phoneType1 = PHONE_TYPE_MOBILE;
    protected String phoneNumber2 = "222-222-2222";
    protected String phoneType2 = PHONE_TYPE_HOME;
    protected String jsonTextArea = "";
    
    /* Other properties */
    protected String fileName;
    protected List<EventRow> rowList;
    static final Logger log = Logger.getLogger("StreamingBean");
    
    public StreamingBean() {}
    
    /* Getters and setters */
    public String getPhoneTypeHome() {
        return PHONE_TYPE_HOME;
    }
    public String getPhoneTypeMobile() {
        return PHONE_TYPE_MOBILE;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firtName) {
        this.firstName = firtName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getStreetAddress() {
        return streetAddress;
    }
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public String getPhoneNumber1() {
        return phoneNumber1;
    }
    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }
    public String getPhoneType1() {
        return phoneType1;
    }
    public void setPhoneType1(String phoneType1) {
        this.phoneType1 = phoneType1;
    }
    public String getPhoneNumber2() {
        return phoneNumber2;
    }
    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }
    public String getPhoneType2() {
        return phoneType2;
    }
    public void setPhoneType2(String phoneType2) {
        this.phoneType2 = phoneType2;
    }
    public String getJsonTextArea() {
        return jsonTextArea;
    }
    public void setJsonTextArea(String jsonTextArea) {
        this.jsonTextArea = jsonTextArea;
    }
    public String getFileName() {
        return fileName;
    }
    public List<EventRow> getRowList() {
        return rowList;
    }

    /* Action method for form in index.xhtml.
     * Writes JSON data using the streaming API. */
    public String writeJson() {
        /* Write JSON data to a file */
        fileName = "jsonoutput-" + (new Random()).nextInt(32000) + ".json";
        try {
            FileWriter writer = new FileWriter(fileName);
            try (JsonGenerator gen = Json.createGenerator(writer)) {
                gen.writeStartObject()
                    .write("firstName", firstName)
                    .write("lastName", lastName)
                    .write("age", age)
                    .write("streetAddress", streetAddress)
                    .write("city", city)
                    .write("state", state)
                    .write("postalCode", postalCode)
                    .writeStartArray("phoneNumbers")
                        .writeStartObject()
                            .write("number", phoneNumber1)
                            .write("type", phoneType1)
                        .writeEnd()
                        .writeStartObject()
                            .write("number", phoneNumber2)
                            .write("type", phoneType2)
                        .writeEnd()
                    .writeEnd()
                .writeEnd();
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "Error writing JSON to file {0}", 
                    fileName + "-" + e.toString());
        }
        
        /* Show resulting JSON in next page */
        jsonTextArea = this.readJsonFile();
        
        /* JSF Navigation */
        return "filewritten";
    }
    
    /* Action method for form in filewritten.xhtml.
     * Parse JSON and populate list of parser events for JSF table */
    public String parseJson() {
        try {
            int nrow = 1;
            rowList = new ArrayList<>();
            JsonParser parser = Json.createParser(new FileReader(fileName));
            while (parser.hasNext()) {
                JsonParser.Event event = parser.next();
                switch(event) {
                    case START_ARRAY:
                    case END_ARRAY:
                    case START_OBJECT:
                    case END_OBJECT:
                    case VALUE_FALSE:
                    case VALUE_NULL:
                    case VALUE_TRUE:
                        rowList.add(new EventRow(nrow++, event.toString(), "--"));
                        break;
                    case KEY_NAME:
                    case VALUE_STRING:
                    case VALUE_NUMBER:
                        rowList.add(new EventRow(nrow++, event.toString(), parser.getString()));
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            log.log(Level.WARNING, "JSON file {0} does not exist", fileName);
        }
        
        /* JSF Navigation */
        return "parsed";
    }
    
    /* Read the JSON file into a String for displaying in JSF page */
    public String readJsonFile() {
        String content = "";
        try {
            String line;
            BufferedReader bread = new BufferedReader(new FileReader(fileName));
            while ((line = bread.readLine()) != null) {
                content = content + line;
            }
        } catch (FileNotFoundException e) {
            log.log(Level.WARNING, "JSON file {0} does not exist", fileName);
        } catch (IOException e) {
            log.log(Level.WARNING, "Error reading from file {0}", fileName);
        }
        return content;
    }
    
    /* Used for showing the events as rows in a JSF table */
    public class EventRow {
        private int number;
        private String type;
        private String details;
        
        public EventRow(int number, String type, String details) {
            this.number = number;
            this.type = type;
            this.details = details;
        }
        
        public int getNumber() { return number; }
        public String getType() { return type; }
        public String getDetails() { return details; }
    }
}
