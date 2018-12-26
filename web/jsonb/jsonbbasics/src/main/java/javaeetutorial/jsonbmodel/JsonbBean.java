/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017-2018 Oracle and/or its affiliates. All rights reserved.
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

/**
 * This class manages the data from a JSF form, creates Object and serialize this object to JSON.
 * This class also deserialize JSON to object.
 *
 * @author David Kral
 */
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

    private Jsonb jsonb;
    
    public JsonbBean() {

        /* JsonbConfig and Jsonb instance creation */
        JsonbConfig config = new JsonbConfig()
                .withFormatting(true);
        this.jsonb = JsonbBuilder.create(config);
    }
    
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
    
    /**
     *  Action method for the form in index.xhtml.
     *  Creates Person object and creates formatted JSON
     */
    public String createJson() {
        Person person = new Person(this.name, this.profession, new ArrayList<>());
        person.getPhoneNumbers().add(new PhoneNumber(this.phoneType1, this.number1));
        person.getPhoneNumbers().add(new PhoneNumber(this.phoneType2, this.number2));

        /* Serialization to JSON */
        this.jsonTextArea = jsonb.toJson(person);
        
        /* JSF navigation */
        return "jsongenerated";
    }
    
    /**
     * Action method for form in jsongenerated.xhtml.
     * Deserialize JSON from the textarea to Person object
     * and fills these data to form.
     */
    public String parseJson() {
        clearFields();

        /* Deserialization of JSON to Object */
        Person person = jsonb.fromJson(this.jsonTextArea, Person.class);
        this.name = person.getName();
        this.profession = person.getProfession();
        if (person.getPhoneNumbers() != null) {
            int index = 0;
            for (PhoneNumber number : person.getPhoneNumbers()) {
                if (index == 0) {
                    this.number1 = number.getNumber();
                    this.phoneType1 = number.getType();
                } else if (index == 1) {
                    this.number2 = number.getNumber();
                    this.phoneType2 = number.getType();
                } else {
                    break;
                }
                index++;
            }
        }

        /* JSF navigation */
        return "index";
    }

    private void clearFields() {
        this.name = "";
        this.profession = "";
        this.number1 = "";
        this.phoneType1 = "";
        this.number2 = "";
        this.phoneType2 = "";
    }

}
