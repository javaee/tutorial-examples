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

import java.util.Properties;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javaeetutorial.batch.webserverlog.items.LogLine;
import javaeetutorial.batch.webserverlog.items.LogFilteredLine;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

/* Processes items from the log file
 * Filters only those items from mobile or tablet browsers,
 * depending on the properties specified in the job definition file.
 */
@Dependent
@Named("LogLineProcessor")
public class LogLineProcessor implements ItemProcessor {

    private String[] browsers;
    private int nbrowsers = 0;
    @Inject
    private JobContext jobCtx;

    public LogLineProcessor() {
    }

    @Override
    public Object processItem(Object item) {
        /* Obtain a list of browsers we are interested in */
        if (nbrowsers == 0) {
            Properties props = jobCtx.getProperties();
            nbrowsers = Integer.parseInt(props.getProperty("num_browsers"));
            browsers = new String[nbrowsers];
            for (int i = 1; i < nbrowsers + 1; i++) {
                browsers[i - 1] = props.getProperty("browser_" + i);
            }
        }

        LogLine logline = (LogLine) item;
        /* Filter for only the mobile/tablet browsers as specified */
        for (int i = 0; i < nbrowsers; i++) {
            if (logline.getBrowser().equals(browsers[i])) {
                /* The new items have fewer fields */
                return new LogFilteredLine(logline);
            }
        }
        return null;
    }
}
