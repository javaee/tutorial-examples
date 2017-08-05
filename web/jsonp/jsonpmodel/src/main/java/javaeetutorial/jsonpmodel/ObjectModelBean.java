/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.jsonpmodel;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.json.*;
import javax.json.stream.JsonGenerator;

/* This class manages the data from a JSF form, creates 
 * a JSON object model from it, and parses JSON data. */
@Named
@SessionScoped
public class ObjectModelBean implements Serializable {
    
    private static final long serialVersionUID = 5427226765445840012L;
    
    /* JSON model information */
    protected String documentJson;
    protected String documentJsonFormatted;
    List<DOMTreeRow> rowList;
    
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
    
    static final Logger log = Logger.getLogger("ObjectModelBean");
    
    public ObjectModelBean() {}
    
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
    public String getDocumentJson() {
        return documentJson;
    }
    public String getDocumentJsonFormatted() {
        return documentJsonFormatted;
    }
    public List<DOMTreeRow> getRowList() {
        return rowList;
    }
    
    /* Action method for the form in index.xhtml.
     * Builds a JSON object model from form data. */
    public String buildJson() {        
        /* Build JSON Object Model */
        JsonObject model = Json.createObjectBuilder()
            .add("firstName", firstName)
            .add("lastName", lastName)
            .add("age", age)
            .add("streetAddress", streetAddress)
            .add("city", city)
            .add("state", state)
            .add("postalCode", postalCode)
            .add("phoneNumbers", Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                    .add("number", phoneNumber1)
                    .add("type", phoneType1))
                .add(Json.createObjectBuilder()
                    .add("number", phoneNumber2)
                    .add("type", phoneType2)))
        .build();
        
        /* Write JSON Output */
        StringWriter stWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stWriter)) {
            jsonWriter.writeObject(model);
        }
        documentJson = stWriter.toString();
        
        /* Write formatted JSON Output */
        Map<String,String> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, "");
        JsonWriterFactory factory = Json.createWriterFactory(config);
        
        StringWriter stWriterF = new StringWriter();
        try (JsonWriter jsonWriterF = factory.createWriter(stWriterF)) {
            jsonWriterF.writeObject(model);
        }
        documentJsonFormatted = stWriterF.toString();
        jsonTextArea = documentJsonFormatted;
        
        /* JSF navigation */
        return "modelcreated";
    }
    
    /* Action method for form in modelcreated.xhtml.
     * Parses JSON data from the textarea. */
    public String parseJson() {
        /* Parse the data using the document object model approach */
        JsonStructure parsed;
        try (JsonReader reader = Json.createReader(new StringReader(jsonTextArea))) {
            parsed = reader.readObject();
        }

        /* Represent the DOM tree on a list for a JSF table */
        rowList = new ArrayList<>();
        this.printTree(parsed, 0, "");
        
        /* JSF navigation */
        return "parsejson";
    }
    
    /* Used to populate rowList to display the DOM tree on a JSF table */
    public void printTree(JsonValue tree, int level, String key) {
        switch (tree.getValueType()) {
            case OBJECT:
                JsonObject object = (JsonObject) tree;
                rowList.add(new DOMTreeRow(level, tree.getValueType().toString(), key, "--"));
                for (String name : object.keySet()) {
                   this.printTree(object.get(name), level+1, name);
                }
                break;
            case ARRAY:
                JsonArray array = (JsonArray) tree;
                rowList.add(new DOMTreeRow(level, tree.getValueType().toString(), key, "--"));
                for (JsonValue val : array) {
                    this.printTree(val, level+1, "");
                }
                break;
            case STRING:
                JsonString st = (JsonString) tree;
                rowList.add(new DOMTreeRow(level, tree.getValueType().toString(), key, st.getString()));
                break;
            case NUMBER:
                JsonNumber num = (JsonNumber) tree;
                rowList.add(new DOMTreeRow(level, tree.getValueType().toString(), key, num.toString()));
                break;
            case FALSE:
            case TRUE:
            case NULL:
                String valtype = tree.getValueType().toString();
                rowList.add(new DOMTreeRow(level, valtype, key, valtype.toLowerCase()));
                break;
        }
    }
    
    /* Used for showing the JSON DOM tree as rows in a JSF table */
    public class DOMTreeRow {
        private int level;
        private String type;
        private String name;
        private String value;
        
        public DOMTreeRow(int level, String type, String name, String value) {
            this.level = level;
            this.type = type;
            this.name = name;
            this.value = value;
        }
        
        public int getLevel() { return level; }
        public String getType() { return type; }
        public String getName() { return name; }
        public String getValue() { return value; }
    }
}
