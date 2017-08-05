/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.batch.webserverlog;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.List;
import javaeetutorial.batch.webserverlog.items.LogFilteredLine;
import javax.batch.api.chunk.ItemWriter;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

/* Write the filtered items */
@Dependent
@Named("LogFilteredLineWriter")
public class LogFilteredLineWriter implements ItemWriter {

    private String fileName;
    private BufferedWriter bwriter;
    @Inject 
    private JobContext jobCtx;

    @Override
    public void open(Serializable ckpt) throws Exception {
        
        fileName = jobCtx.getProperties().getProperty("filtered_file_name");
        /* If the job was restarted, continue writing at the end of the file.
         * Otherwise, overwrite the file. */
        if (ckpt != null)
            bwriter = new BufferedWriter(new FileWriter(fileName, true));
        else
            bwriter = new BufferedWriter(new FileWriter(fileName, false));
    }

    @Override
    public void close() throws Exception {
        bwriter.close();
    }

    @Override
    public void writeItems(List<Object> items) throws Exception {
        /* Write the filtered lines to the output file */
        for (int i = 0; i < items.size(); i++) {
            LogFilteredLine filtLine = (LogFilteredLine) items.get(i);
            bwriter.write(filtLine.toString());
            bwriter.newLine();
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return new ItemNumberCheckpoint();
    }
}
