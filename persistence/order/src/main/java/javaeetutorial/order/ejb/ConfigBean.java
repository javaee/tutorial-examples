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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaeetutorial.order.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author ian
 */
@Singleton
@Startup
public class ConfigBean {

    @EJB
    private RequestBean request;

    @PostConstruct
    public void createData() {
        request.createPart("1234-5678-01", 1, "ABC PART",
                new java.util.Date(), "PARTQWERTYUIOPASXDCFVGBHNJMKL", null);
        request.createPart("9876-4321-02", 2, "DEF PART",
                new java.util.Date(), "PARTQWERTYUIOPASXDCFVGBHNJMKL", null);
        request.createPart("5456-6789-03", 3, "GHI PART",
                new java.util.Date(), "PARTQWERTYUIOPASXDCFVGBHNJMKL", null);
        request.createPart("ABCD-XYZW-FF", 5, "XYZ PART",
                new java.util.Date(), "PARTQWERTYUIOPASXDCFVGBHNJMKL", null);
        request.createPart("SDFG-ERTY-BN", 7, "BOM PART",
                new java.util.Date(), "PARTQWERTYUIOPASXDCFVGBHNJMKL", null);

        request.addPartToBillOfMaterial("SDFG-ERTY-BN", 7,
                "1234-5678-01", 1);
        request.addPartToBillOfMaterial("SDFG-ERTY-BN", 7,
                "9876-4321-02", 2);
        request.addPartToBillOfMaterial("SDFG-ERTY-BN", 7,
                "5456-6789-03", 3);
        request.addPartToBillOfMaterial("SDFG-ERTY-BN", 7,
                "ABCD-XYZW-FF", 5);

        request.createVendor(100, "WidgetCorp",
                "111 Main St., Anytown, KY 99999", "Mr. Jones",
                "888-777-9999");
        request.createVendor(200, "Gadget, Inc.",
                "123 State St., Sometown, MI 88888", "Mrs. Smith",
                "866-345-6789");

        request.createVendorPart("1234-5678-01", 1,
                "PART1", 100.00, 100);
        request.createVendorPart("9876-4321-02", 2,
                "PART2", 10.44, 200);
        request.createVendorPart("5456-6789-03", 3,
                "PART3", 76.23, 200);
        request.createVendorPart("ABCD-XYZW-FF", 5,
                "PART4", 55.19, 100);
        request.createVendorPart("SDFG-ERTY-BN", 7,
                "PART5", 345.87, 100);

        Integer orderId = new Integer(1111);
        request.createOrder(orderId, 'N', 10,
                "333 New Court, New City, CA 90000");
        request.addLineItem(orderId, "1234-5678-01", 1, 3);
        request.addLineItem(orderId, "9876-4321-02", 2, 5);
        request.addLineItem(orderId, "ABCD-XYZW-FF", 5, 7);

        orderId = new Integer(4312);
        request.createOrder(orderId, 'N', 0,
                "333 New Court, New City, CA 90000");
        request.addLineItem(orderId, "SDFG-ERTY-BN", 7, 1);
        request.addLineItem(orderId, "ABCD-XYZW-FF", 5, 3);
        request.addLineItem(orderId, "1234-5678-01", 1, 15);
    }

    @PreDestroy
    public void deleteData() {
        
    }
}
