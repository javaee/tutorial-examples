/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.batch.phonebilling;

import java.util.Properties;
import javax.batch.api.partition.PartitionMapper;
import javax.batch.api.partition.PartitionPlan;
import javax.batch.api.partition.PartitionPlanImpl;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/* Partition mapper artifact.
 * Determines the number of partitions (2) for the bill processing step
 * and the range of bills each partition should work on.
 */
@Dependent
@Named("BillPartitionMapper")
public class BillPartitionMapper implements PartitionMapper {

    @PersistenceContext
    EntityManager em;

    @Override
    public PartitionPlan mapPartitions() throws Exception {
        /* Create a new partition plan */
        return new PartitionPlanImpl() {

            /* Auxiliary method - get the number of bills */
            public long getBillCount() {
                String query = "SELECT COUNT(b) FROM PhoneBill b";
                Query q = em.createQuery(query);
                return ((Long) q.getSingleResult()).longValue(); 
            }

            /* The number of partitions could be dynamically calculated based on
             * many parameters. In this particular example, we are setting it to
             * a fixed value for simplicity.
             */
            @Override
            public int getPartitions() {
                return 2;
            }

            @Override
            public Properties[] getPartitionProperties() {
                /* Assign an (approximately) equal number of elements
                 * to each partition. */
                long totalItems = getBillCount();
                long partItems =  totalItems / getPartitions();
                long remItems = totalItems % getPartitions();

                /* Populate a Properties array. Each Properties element
                 * in the array corresponds to a partition. */
                Properties[] props = new Properties[getPartitions()];

                for (int i = 0; i < getPartitions(); i++) {
                    props[i] = new Properties();
                    props[i].setProperty("firstItem", 
                            String.valueOf(i * partItems));
                    /* Last partition gets the remainder elements */
                    if (i == getPartitions() - 1) {
                        props[i].setProperty("numItems", 
                                String.valueOf(partItems + remItems));
                    } else {
                        props[i].setProperty("numItems", 
                                String.valueOf(partItems));
                    }
                }
                return props;
            }
        };
    }

}
