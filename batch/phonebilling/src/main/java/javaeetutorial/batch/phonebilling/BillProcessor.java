/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.batch.phonebilling;

import java.math.BigDecimal;
import javaeetutorial.batch.phonebilling.items.PhoneBill;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

/* Processor artifact for bills.
 * Compute amount and total for each bill
 */
@Dependent
@Named("BillProcessor")
public class BillProcessor implements ItemProcessor {
    
    @Inject
    JobContext jobCtx;

    @Override
    public Object processItem(Object billObject) throws Exception {

        String s_taxRate = jobCtx.getProperties().get("tax_rate").toString();
        double taxRate = Double.parseDouble(s_taxRate);
        PhoneBill bill = (PhoneBill) billObject;
        bill.calculate(new BigDecimal(taxRate));
        return bill;
    }
    
}
