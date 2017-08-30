/**
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.jsonbmodel.person;

import java.util.List;

/**
 * Person object
 *
 * @author David Kral
 */
public class Person {
    private String name;
    private String profession;

    private List<PhoneNumber> phoneNumbers;

    public Person() {
    }

    public Person(String name, String profession, List<PhoneNumber> phoneNumbers) {
        this.name = name;
        this.profession = profession;
        this.phoneNumbers = phoneNumbers;
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

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", profession='" + profession + '\'' +
                ", phoneNumbers=" + phoneNumbers +
                '}';
    }
}
