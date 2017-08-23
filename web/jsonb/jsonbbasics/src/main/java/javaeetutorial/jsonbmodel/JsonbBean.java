/**
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.jsonbmodel;

import javaeetutorial.jsonbmodel.person.Person;
import javaeetutorial.jsonbmodel.person.PhoneNumber;

import java.io.*;
import java.util.*;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

/* This class manages the data from a JSF form, creates 
 * Object and serialize this object to JSON. This class
  * also deserialize JSON to object */
@Named
@SessionScoped
public class JsonbBean implements Serializable {
    
    private static final long serialVersionUID = 5427226765445840012L;
    
    /* Form properties */
    protected static final String PHONE_TYPE_HOME = "Home";
    protected static final String PHONE_TYPE_WORK = "Work";

    private String name = "Jason Bourne";
    private String profession = "Super agent";

    private String phoneType1 = PHONE_TYPE_HOME;
    private String number1 = "123-456-789";

    private String phoneType2 = PHONE_TYPE_WORK;
    private String number2 = "123-555-555";

    protected String jsonTextArea = "";
    
    public JsonbBean() {}
    
    /* Getters and setters */
    public String getPhoneTypeHome() {
        return PHONE_TYPE_HOME;
    }
    public String getPhoneTypeWork() {
        return PHONE_TYPE_WORK;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getProfession() {
        return profession;
    }
    public void setProfession(String profession) {
        this.profession = profession;
    }
    public String getPhoneType1() {
        return phoneType1;
    }
    public void setPhoneType1(String phoneType1) {
        this.phoneType1 = phoneType1;
    }
    public String getNumber1() {
        return number1;
    }
    public void setNumber1(String number1) {
        this.number1 = number1;
    }
    public String getPhoneType2() {
        return phoneType2;
    }
    public void setPhoneType2(String phoneType2) {
        this.phoneType2 = phoneType2;
    }
    public String getNumber2() {
        return number2;
    }
    public void setNumber2(String number2) {
        this.number2 = number2;
    }
    public String getJsonTextArea() {
        return jsonTextArea;
    }
    public void setJsonTextArea(String jsonTextArea) {
        this.jsonTextArea = jsonTextArea;
    }
    
    /* Action method for the form in index.xhtml.
     * Creates Person object and creates formatted JSON */
    public String createJson() {
        Person person = new Person(name, profession, new ArrayList<>());
        person.getPhoneNumbers().add(new PhoneNumber(phoneType1, number1));
        person.getPhoneNumbers().add(new PhoneNumber(phoneType2, number2));

        /* JSON-B config and Jsonb instance creating */
        JsonbConfig config = new JsonbConfig()
                .withFormatting(true);
        Jsonb jsonb = JsonbBuilder.create(config);

        /* Deserialization to JSON */
        jsonTextArea = jsonb.toJson(person);
        
        /* JSF navigation */
        return "jsongenerated";
    }
    
    /* Action method for form in jsongenerated.xhtml.
     * Parses JSON data from the textarea to Person object
     * and fills these data to form. */
    public String parseJson() {
        /* JSON-B config and Jsonb instance creating */
        JsonbConfig config = new JsonbConfig()
                .withFormatting(true);
        Jsonb jsonb = JsonbBuilder.create(config);

        /* Deserialization of JSON */
        Person person = jsonb.fromJson(jsonTextArea, Person.class);
        name = person.getName();
        profession = person.getProfession();
        PhoneNumber number = person.getPhoneNumbers().get(0);
        if (number != null) {
            number1 = number.getNumber();
            phoneType1 = number.getType();
        }
        number = person.getPhoneNumbers().get(1);
        if (number != null) {
            number2 = number.getNumber();
            phoneType2 = number.getType();
        }

        /* JSF navigation */
        return "index";
    }

}
