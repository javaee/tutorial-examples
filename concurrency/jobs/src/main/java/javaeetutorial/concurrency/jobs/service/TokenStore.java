/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.concurrency.jobs.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

/**
 * @author markito
 */
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Singleton
public class TokenStore implements Serializable {

    private final List<String> store;

    public TokenStore() {
        this.store = new ArrayList<>();
    }

    @Lock(LockType.WRITE)
    public void put(String key) {
        store.add(key);
    }

    @Lock(LockType.READ)
    public boolean isValid(String key) {
        return store.contains(key);
    }
}
