/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.batch.phonebilling;

import java.io.Serializable;
import java.util.Iterator;
import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemReader;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/* Reader batch artifact.
 * Reads bills from the entity manager.
 * This artifact is in a partitioned step.
 */
@Dependent
@Named("BillReader")
public class BillReader implements ItemReader {
    @Inject
    @BatchProperty(name = "firstItem")
    private String firstItemValue;

    @Inject
    @BatchProperty(name = "numItems")
    private String numItemsValue;

    private ItemNumberCheckpoint checkpoint;

    @PersistenceContext
    private EntityManager em;
    private Iterator iterator;

    public BillReader() {
    }

    @Override
    public void open(Serializable ckpt) throws Exception {

        /* Get the range of items to work on in this partition */
        long firstItem0 = Long.parseLong(firstItemValue);
        long numItems0 = Long.parseLong(numItemsValue);

        if (ckpt == null) {
            /* Create a checkpoint object for this partition */
            checkpoint = new ItemNumberCheckpoint();
            checkpoint.setItemNumber(firstItem0);
            checkpoint.setNumItems(numItems0);
        } else {
            checkpoint = (ItemNumberCheckpoint) ckpt;
        }

        /* Adjust range for this partition from the checkpoint */
        long firstItem = checkpoint.getItemNumber();
        long numItems = numItems0 - (firstItem - firstItem0);

        /* Obtain an iterator for the bills in this partition */
        String query = "SELECT b FROM PhoneBill b ORDER BY b.phoneNumber";
        Query q = em.createQuery(query).setFirstResult((int) firstItem)
                .setMaxResults((int) numItems);
        iterator = q.getResultList().iterator();
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public Object readItem() throws Exception {
        if (iterator.hasNext()) {
            checkpoint.nextItem();
            checkpoint.setNumItems(checkpoint.getNumItems() - 1);
            return iterator.next();
        } else {
            return null;
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return checkpoint;
    }

}
