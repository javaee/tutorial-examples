/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.batch.phonebilling;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import javaeetutorial.batch.phonebilling.items.CallRecord;
import javax.batch.api.chunk.ItemReader;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

/* Reader batch artifact.
 * Reads call records from the input log file.
 */
@Dependent
@Named("CallRecordReader")
public class CallRecordReader implements ItemReader {

    private ItemNumberCheckpoint checkpoint;
    private String fileName;
    private BufferedReader breader;
    @Inject
    JobContext jobCtx;
    
    public CallRecordReader() { }
    
    @Override
    public void open(Serializable ckpt) throws Exception {
        /* Use the checkpoint provided if this is a restart */
        if (ckpt == null)
            checkpoint = new ItemNumberCheckpoint();
        else
            checkpoint = (ItemNumberCheckpoint) ckpt;
        
        /* Read the input file up to the checkpoint without processing */
        fileName = jobCtx.getProperties().getProperty("log_file_name");
        breader = new BufferedReader(new FileReader(fileName));
        for (int i=0; i<checkpoint.getItemNumber(); i++)
            breader.readLine();
    }

    @Override
    public void close() throws Exception {
        breader.close();
    }

    @Override
    public Object readItem() throws Exception {
        /* Read a line from the log file and 
         * create a CallRecord from JSON */
        String callEntryJson = breader.readLine();
        if (callEntryJson != null) {
            checkpoint.nextItem();
            return new CallRecord(callEntryJson);
        } else
            return null;
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return checkpoint;
    }
    
}
