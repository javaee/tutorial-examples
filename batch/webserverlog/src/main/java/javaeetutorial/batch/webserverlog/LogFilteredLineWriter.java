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
