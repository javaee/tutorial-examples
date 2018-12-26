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

package javaeetutorial.trading.rar.outbound;

import java.io.IOException;
import java.util.logging.Logger;
import javaeetutorial.trading.rar.api.TradeConnection;
import javaeetutorial.trading.rar.api.TradeOrder;
import javaeetutorial.trading.rar.api.TradeProcessingException;
import javaeetutorial.trading.rar.api.TradeResponse;
import javax.resource.ResourceException;

/* An application-level connection handle used by clients to access
 * the physical connection. The physical connection is represented by
 * a ManagedConnection instance */
public class TradeConnectionImpl implements TradeConnection {
    
    private static final Logger log = Logger.getLogger("TradeConnectionImpl");
    private TradeManagedConnection mconnection;
    private boolean valid;
    
    /* */
    TradeConnectionImpl(TradeManagedConnection mconnection) {
        this.mconnection = mconnection;
        valid = true;
    }
    
    /* Called by the managed connection to dis/associate this handle. */
    TradeManagedConnection getManagedConnection() {
        return mconnection;
    }
    void setManagedConnection(TradeManagedConnection mconnection) {
        this.mconnection = mconnection;
    }
    
    /* Called by the managed connection to invalidate this handle */
    void invalidate() {
        valid = false;
    }

    /* Submits a trade order to the EIS */
    @Override
    public TradeResponse submitOrder(TradeOrder order) 
                                     throws TradeProcessingException {
        log.info("[TradeConnectionImpl] submitOrder()");
        if (valid) {
            try {
                String resp = mconnection.sendCommandToEIS(order.toString());
                return new TradeResponse(resp);
            } catch (IOException e) {
                throw new TradeProcessingException(e.getMessage());
            }
        } else
            throw new TradeProcessingException("Connection handle is invalid");
    }
    
    /* Closes the connection handle */
    @Override
    public void close() throws ResourceException {
        log.info("[TradeConnectionImpl] close()");
        valid = false;
        mconnection.disassociateConnection();
    }
    
}
