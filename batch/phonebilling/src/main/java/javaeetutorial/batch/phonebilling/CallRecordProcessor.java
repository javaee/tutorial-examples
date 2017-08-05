/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.batch.phonebilling;

import java.math.BigDecimal;
import javaeetutorial.batch.phonebilling.items.CallRecord;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

/* Processor batch artifact.
 * Calculate the price of every call.
 */
@Dependent
@Named("CallRecordProcessor")
public class CallRecordProcessor implements ItemProcessor {
    
    @Inject
    JobContext jobCtx;
    double airPrice;
    
    public CallRecordProcessor() { }

    @Override
    public Object processItem(Object obj) throws Exception {
        CallRecord call;
        double callPrice;
        
        /* Calculate the price of this call */
        String s_airPrice = jobCtx.getProperties().getProperty("airtime_price");
        airPrice = Double.parseDouble(s_airPrice);
        call = (CallRecord) obj;
        callPrice = airPrice*(1.0*call.getMinutes() + call.getSeconds()/60.0);
        call.setPrice(new BigDecimal(callPrice));
        return call;
    }
    
}
