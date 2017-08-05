/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.batch.phonebilling;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import javaeetutorial.batch.phonebilling.items.CallRecord;
import javaeetutorial.batch.phonebilling.items.PhoneBill;
import javax.batch.api.chunk.ItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

/* Writer artifact.
 * Write each bill to a text file.
 */
@Dependent
@Named("BillWriter")
public class BillWriter implements ItemWriter {
    
    @Override
    public void open(Serializable ckpt) throws Exception { }

    @Override
    public void close() throws Exception { }

    @Override
    public void writeItems(List<Object> list) throws Exception {
        for (Object billObject : list) {
            PhoneBill bill = (PhoneBill) billObject;
            FileWriter fwriter = new FileWriter(bill.getPhoneNumber()+".txt");
            try (BufferedWriter bwriter = new BufferedWriter(fwriter)) {
                bwriter.write("DUKE WIRELESS - ACCCOUNT " + bill.getPhoneNumber());
                bwriter.newLine();
                bwriter.write(" ");
                bwriter.newLine();
                bwriter.write("Date            \tFrom    \tTo      \tLength\tPrice");
                bwriter.newLine();
                for (CallRecord call : bill.getCalls()) {
                    SimpleDateFormat dformat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                    String date = dformat.format(call.getDatetime());
                    bwriter.write(String.format("%s\t%s\t%s\t%02d:%02d\t%.2f", 
                                                date,
                                                call.getFromNumber(),
                                                call.getToNumber(),
                                                call.getMinutes(),
                                                call.getSeconds(),
                                                call.getPrice()));
                    bwriter.newLine();
                }
                bwriter.write(" ");
                bwriter.newLine();
                bwriter.write(String.format("Base    \t%.2f", bill.getAmountBase()));
                bwriter.newLine();
                bwriter.write(String.format("Tax rate\t%.2f%%", bill.getTaxRate().doubleValue()*100));
                bwriter.newLine();
                bwriter.write(String.format("Tax     \t%.2f", bill.getTax()));
                bwriter.newLine();
                bwriter.write(String.format("Total   \t%.2f", bill.getAmountTotal()));
            }
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return new ItemNumberCheckpoint();
    }
    
}
