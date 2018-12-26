/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
