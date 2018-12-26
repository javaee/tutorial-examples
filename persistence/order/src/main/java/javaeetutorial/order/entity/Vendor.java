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

package javaeetutorial.order.entity;

import java.util.Collection;
import static javax.persistence.CascadeType.ALL;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="PERSISTENCE_ORDER_VENDOR")
@NamedQueries({
    @NamedQuery(
        name="findVendorsByPartialName",
        query="SELECT v " +
              "FROM Vendor v " +
              "WHERE LOCATE(:name, v.name) > 0"
    ),
    @NamedQuery(
        name="findVendorByCustomerOrder",
        query="SELECT DISTINCT l.vendorPart.vendor " +
              "FROM CustomerOrder co, IN(co.lineItems) l " +
              "WHERE co.orderId = :id " +
              "ORDER BY l.vendorPart.vendor.name"
    )
})
public class Vendor implements java.io.Serializable {
    private static final long serialVersionUID = 2538635007598426330L;
    private int vendorId;
    private String name;
    private String address;
    private String contact;
    private String phone;
    private Collection<VendorPart> vendorParts;
    
    public Vendor() {}
    
    public Vendor(int vendorId, 
            String name, 
            String address, 
            String contact, 
            String phone) {
        this.vendorId = vendorId;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.phone = phone;
    }

    @Id
    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    @Column(name="VENDORNAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @OneToMany(cascade=ALL, mappedBy="vendor")
    public Collection<VendorPart> getVendorParts() {
        return vendorParts;
    }

    public void setVendorParts(Collection<VendorPart> vendorParts) {
        this.vendorParts = vendorParts;
    }
    
    public void addVendorPart(VendorPart vendorPart) {
        this.getVendorParts().add(vendorPart);
    }
}
