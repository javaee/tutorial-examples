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

package javaeetutorial.dukesbookstore.web.managedbeans;

import java.io.Serializable;
import javaeetutorial.dukesbookstore.entity.Book;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * <p>Backing bean for the <code>/bookshowcart.xhtml</code> page.</p>
 */
@Named("showcart")
@SessionScoped
public class ShowCartBean extends AbstractBean implements Serializable {

    private static final long serialVersionUID = 2287968973374093614L;
    private boolean cartChanged = false;

    public boolean isCartChanged() {
        return cartChanged;
    }

    public void setCartChanged(boolean cartChanged) {
        this.cartChanged = cartChanged;
    }

    /**
     * @return the <code>ShoppingCartItem</code> instance from the user request
     */
    protected ShoppingCartItem item() {
        ShoppingCartItem item = (ShoppingCartItem) context()
                .getExternalContext().getRequestMap().get("item");

        return (item);
    }

    /**
     * <p>Show the details page for the current book.</p>
     * @return the navigation page
     */
    public String details() {
        context().getExternalContext().getSessionMap()
                .put("selected", item().getItem());

        return ("bookdetails");
    }

    /**
     * <p>Remove the item from the shopping cart.</p>
     * @return null string
     */
    public String remove() {
        Book book = (Book) item().getItem();
        cart.remove(book.getBookId());
        setCartChanged(true);
        message(null, "ConfirmRemove", new Object[]{book.getTitle()});

        return (null);
    }

    /**
     * <p>Report the change to the shopping cart, based on the values entered in
     * the "Quantity" column.</p>
     * @return null string
     */
    public String update() {
        String changed = (String) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("changed");
        if ((changed != null) && changed.equals("true")) {
            setCartChanged(true);
        } else {
            setCartChanged(false);
        }
        if (isCartChanged()) {
            message(null, "QuantitiesUpdated");
        } else {
            message(null, "QuantitiesNotUpdated");
        }
        FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().put("changed", "false");
        return (null);
    }
}
