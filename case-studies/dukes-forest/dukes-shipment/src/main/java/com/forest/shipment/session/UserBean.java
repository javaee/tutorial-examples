/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package com.forest.shipment.session;

import com.forest.entity.Customer;
import com.forest.entity.Person;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author markito
 */
@Stateless
public class UserBean extends AbstractFacade<Customer> {
    
    @PersistenceContext(unitName="forestPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public Person getUserByEmail(String email) {
        Query createNamedQuery = getEntityManager().createNamedQuery("Person.findByEmail");
        
        createNamedQuery.setParameter("email", email);
        
        return (Person) createNamedQuery.getSingleResult();
    }
    
    public UserBean() {
        super(Customer.class);
    }

}
