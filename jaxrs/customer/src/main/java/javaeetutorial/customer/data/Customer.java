/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.customer.data;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.*;

/**
 *
 * @author ievans
 */
@Entity
@Table(name="CUSTOMER_CUSTOMER")
@NamedQuery(
    name="findAllCustomers",
    query="SELECT c FROM Customer c " +
          "ORDER BY c.id"
)
@XmlRootElement(name="customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class Customer implements Serializable {
    private static final Logger logger = 
            Logger.getLogger(Customer.class.getName());
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlAttribute(required=true) 
    protected int id;
    
    @XmlElement(required=true) 
    protected String firstname;
    
    @XmlElement(required=true) 
    protected String lastname;
    
    @XmlElement(required=true)
    @OneToOne
    protected Address address;
    
    @XmlElement(required=true)
    protected String email;
 
    @XmlElement (required=true)
    protected String phone;
    
    public Customer() { 
        address = new Address();
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        logger.log(Level.INFO, "setId called and set to {0}", id);
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        logger.log(Level.INFO, "setFirstname called and set to {0}", firstname);
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastName) {
        logger.log(Level.INFO, "setLastname called and set to {0}", lastName);
        this.lastname = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        logger.log(Level.INFO, "setAddress called");
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        logger.log(Level.INFO, "setEmail called and set to {0}", email);
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        logger.log(Level.INFO, "setPhone called and set to {0}", phone);
        this.phone = phone;
    }
}
