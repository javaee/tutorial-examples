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

package javaeetutorial.traffic.rar.inbound;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javaeetutorial.traffic.rar.api.TrafficCommand;
import javaeetutorial.traffic.rar.api.TrafficListener;
import javax.resource.ResourceException;
import javax.resource.spi.Activation;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;

/* The activation spec used by the MDB to configure the resource adapter */
@Activation(
        messageListeners = { TrafficListener.class }
)
public class TrafficActivationSpec implements ActivationSpec, Serializable {

    private ResourceAdapter ra;
    @ConfigProperty()
    private String port;
    private Class beanClass;
    private Map<String,Method> commands;
    private static final long serialVersionUID = 1674967719558213103L;
    private static final Logger log = Logger.getLogger("TrafficActivationSpec");
    
    public TrafficActivationSpec() throws InvalidPropertyException {
        commands = new HashMap<>();
    }
    
    /* Port is set by the MDB using @ActivationConfigProperty */
    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }
    
    /* Set from the RA class and accessed by the traffic subscriber thread */
    public void setBeanClass(Class c) { beanClass = c; }
    public Class getBeanClass() { return beanClass; }
    
    /* Inspect the MDB class for methods with a custom annotation.
     * This allows the MDB business interface to be emtpy */    
    public void findCommandsInMDB() {
        log.info("[TrafficActivationSpec] findCommandsInMDB()");
        for (Method method : beanClass.getMethods()) {
            if (method.isAnnotationPresent(TrafficCommand.class)) {
                TrafficCommand tCommand = method.getAnnotation(TrafficCommand.class);
                commands.put(tCommand.name(), method);
            }
        }
        
        if (commands.isEmpty())
            log.info("No command annotations in MDB.");
        
        for (Method m : commands.values()) {
            for (Class c : m.getParameterTypes())
                if (c != String.class)
                    log.info("Command args must be String.");
        }
    }
    
    /* Used by the subscriber thread to invoke the discovered commands on the MDB */
    public Map<String,Method> getCommands() { return commands; }
    
    @Override
    public void validate() throws InvalidPropertyException { }

    @Override
    public ResourceAdapter getResourceAdapter() {
        return ra;
    }

    @Override
    public void setResourceAdapter(ResourceAdapter ra) throws ResourceException {
        log.info("[TrafficActivationSpec] setResourceAdapter()");
        this.ra = ra;
    }
    
}
