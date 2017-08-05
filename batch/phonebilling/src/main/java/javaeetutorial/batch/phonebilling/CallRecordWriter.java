/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.batch.phonebilling;

import java.io.Serializable;
import java.util.List;
import javaeetutorial.batch.phonebilling.items.CallRecord;
import javaeetutorial.batch.phonebilling.items.PhoneBill;
import javax.batch.api.chunk.ItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/* Writer batch artifact.
 * Add every call to a bill entity.
 */
@Dependent
@Named("CallRecordWriter")
public class CallRecordWriter implements ItemWriter {
    
    @PersistenceContext
    EntityManager em;
    
    public CallRecordWriter() {}

    @Override
    public void open(Serializable checkpoint) throws Exception {
        /* Clear all bills if this is not a restart of the job */
        if (checkpoint == null)
            em.clear();
    }

    @Override
    public void close() throws Exception { }

    @Override
    public void writeItems(List<Object> callList) throws Exception {
        
        for (Object callObject : callList) {
            CallRecord call = (CallRecord) callObject;
            PhoneBill bill = em.find(PhoneBill.class, call.getFromNumber());
            if (bill == null) {
                /* No bill for this customer yet, create one */
                bill = new PhoneBill(call.getFromNumber());
                bill.addCall(call);
                em.persist(bill);
            } else {
                /* Add call to existing bill */
                bill.addCall(call);
            }
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return new ItemNumberCheckpoint();
    }
    
}
