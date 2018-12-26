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

package javaeetutorial.batch.phonebilling.items;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/* This class is a Java Persistence API entity that
 * represents a phone bill in the batch application.
 */
@Entity
public class PhoneBill implements Serializable {

    @Id
    private String phoneNumber;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @OrderBy("datetime ASC")
    private List<CallRecord> calls;
    private BigDecimal amountBase;
    private BigDecimal taxRate;
    private BigDecimal tax;
    private BigDecimal amountTotal;
    private static final long serialVersionUID = -7617311744533479326L;

    public PhoneBill() {
    }
    
    public PhoneBill(String number) {
        this.phoneNumber = number;
        calls = new ArrayList<>();
    }
    
    public void addCall(CallRecord call) {
        calls.add(call);
    }
    
    public void calculate(BigDecimal taxRate) {
        /* Compute the total amount and tax */
        this.taxRate = taxRate;
        amountBase = new BigDecimal(0).setScale(2);
        for (CallRecord call : calls) {
            amountBase = amountBase.add(call.getPrice());
        }
        tax = amountBase.multiply(taxRate).setScale(2, RoundingMode.HALF_EVEN);
        amountTotal = amountBase.add(tax);
    }
    
    public String getPhoneNumber() { return phoneNumber; }
    public List<CallRecord> getCalls() { return calls; }
    public BigDecimal getAmountBase() { return amountBase; }
    public BigDecimal getTax() { return tax; }
    public BigDecimal getTaxRate() { return taxRate; }
    public BigDecimal getAmountTotal() { return amountTotal; }
}
